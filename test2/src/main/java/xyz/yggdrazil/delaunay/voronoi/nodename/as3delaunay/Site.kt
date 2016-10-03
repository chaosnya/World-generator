package xyz.yggdrazil.delaunay.voronoi.nodename.as3delaunay

import xyz.yggdrazil.delaunay.geom.Point
import xyz.yggdrazil.delaunay.geom.Rectangle

import java.awt.*
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.Stack

class Site(p: Point, index: Int, weight: Double, var color: Color?) : ICoord {

    override var coord: Point? = null
    var weight: Double = 0.toDouble()
    // the edges that define this Site's Voronoi region:
    var edges: ArrayList<Edge>? = null

    private var _siteIndex: Int = 0
    // which end of each edge hooks up with the previous edge in edges:
    private var _edgeOrientations: ArrayList<LR>? = null
    // ordered list of points that define the region clipped to bounds:
    private var _region: ArrayList<Point>? = null

    init {
        init(p, index, weight, color)
    }

    private fun init(p: Point, index: Int, weight: Double, color: Color?): Site {
        coord = p
        _siteIndex = index
        this.weight = weight
        this.color = color
        edges = ArrayList()
        _region = null
        return this
    }

    override fun toString(): String {
        return "Site $_siteIndex: $coord"
    }

    private fun move(p: Point) {
        clear()
        coord = p
    }

    fun dispose() {
        coord = null
        clear()
        pool.push(this)
    }

    private fun clear() {
        if (edges != null) {
            edges!!.clear()
            edges = null
        }
        if (_edgeOrientations != null) {
            _edgeOrientations!!.clear()
            _edgeOrientations = null
        }
        if (_region != null) {
            _region!!.clear()
            _region = null
        }
    }

    internal fun addEdge(edge: Edge) {
        edges!!.add(edge)
    }

    fun nearestEdge(): Edge {
        // edges.sort(Edge.compareSitesDistances);
        Collections.sort(edges!!) { o1, o2 -> Edge.compareSitesDistances(o1, o2).toInt() }
        return edges!![0]
    }

    internal fun neighborSites(): ArrayList<Site?> {
        if (edges == null || edges!!.isEmpty()) {
            return ArrayList()
        }
        if (_edgeOrientations == null) {
            reorderEdges()
        }
        val list = ArrayList<Site?>()
        for (edge in edges!!) {
            list.add(neighborSite(edge))
        }
        return list
    }

    private fun neighborSite(edge: Edge): Site? {
        if (this == edge.leftSite) {
            return edge.rightSite
        }
        if (this == edge.rightSite) {
            return edge.leftSite
        }
        return null
    }

    internal fun region(clippingBounds: Rectangle): ArrayList<Point>? {
        if (edges == null || edges!!.isEmpty()) {
            return ArrayList()
        }
        if (_edgeOrientations == null) {
            reorderEdges()
            _region = clipToBounds(clippingBounds)
            if (Polygon(_region!!).winding() == Winding.CLOCKWISE) {
                Collections.reverse(_region!!)
            }
        }
        return _region
    }

    private fun reorderEdges() {
        //trace("edges:", edges);
        val reorderer = EdgeReorderer(edges!!, Vertex::class.java)
        edges = reorderer.edges
        //trace("reordered:", edges);
        _edgeOrientations = reorderer.edgeOrientations
        reorderer.dispose()
    }

    private fun clipToBounds(bounds: Rectangle): ArrayList<Point> {
        val points = ArrayList<Point>()
        val n = edges!!.size
        var i = 0
        var edge: Edge
        while (i < n && !edges!![i].visible) {
            ++i
        }

        if (i == n) {
            // no edges visible
            return ArrayList()
        }
        edge = edges!![i]
        val orientation = _edgeOrientations!![i]
        points.add(edge.clippedEnds!![orientation]!!)
        points.add(edge.clippedEnds!![LR.other(orientation)]!!)

        for (j in i + 1..n - 1) {
            edge = edges!![j]
            if (!edge.visible) {
                continue
            }
            connect(points, j, bounds, false)
        }
        // close up the polygon by adding another corner point of the bounds if needed:
        connect(points, i, bounds, true)

        return points
    }

    private fun connect(points: ArrayList<Point>, j: Int, bounds: Rectangle, closingUp: Boolean) {
        val rightPoint = points[points.size - 1]
        val newEdge = edges!![j]
        val newOrientation = _edgeOrientations!![j]
        // the point that  must be connected to rightPoint:
        val newPoint = newEdge.clippedEnds!![newOrientation]
        if (!closeEnough(rightPoint, newPoint!!)) {
            // The points do not coincide, so they must have been clipped at the bounds;
            // see if they are on the same border of the bounds:
            if (rightPoint.x != newPoint.x && rightPoint.y != newPoint.y) {
                // They are on different borders of the bounds;
                // insert one or two corners of bounds as needed to hook them up:
                // (NOTE this will not be correct if the region should take up more than
                // half of the bounds rect, for then we will have gone the wrong way
                // around the bounds and included the smaller part rather than the larger)
                val rightCheck = BoundsCheck.check(rightPoint, bounds)
                val newCheck = BoundsCheck.check(newPoint!!, bounds)
                val px: Double
                val py: Double
                if (rightCheck and BoundsCheck.RIGHT != 0) {
                    px = bounds.right
                    if (newCheck and BoundsCheck.BOTTOM != 0) {
                        py = bounds.bottom
                        points.add(Point(px, py))
                    } else if (newCheck and BoundsCheck.TOP != 0) {
                        py = bounds.top
                        points.add(Point(px, py))
                    } else if (newCheck and BoundsCheck.LEFT != 0) {
                        if (rightPoint.y - bounds.y + newPoint.y - bounds.y < bounds.height) {
                            py = bounds.top
                        } else {
                            py = bounds.bottom
                        }
                        points.add(Point(px, py))
                        points.add(Point(bounds.left, py))
                    }
                } else if (rightCheck and BoundsCheck.LEFT != 0) {
                    px = bounds.left
                    if (newCheck and BoundsCheck.BOTTOM != 0) {
                        py = bounds.bottom
                        points.add(Point(px, py))
                    } else if (newCheck and BoundsCheck.TOP != 0) {
                        py = bounds.top
                        points.add(Point(px, py))
                    } else if (newCheck and BoundsCheck.RIGHT != 0) {
                        if (rightPoint.y - bounds.y + newPoint.y - bounds.y < bounds.height) {
                            py = bounds.top
                        } else {
                            py = bounds.bottom
                        }
                        points.add(Point(px, py))
                        points.add(Point(bounds.right, py))
                    }
                } else if (rightCheck and BoundsCheck.TOP != 0) {
                    py = bounds.top
                    if (newCheck and BoundsCheck.RIGHT != 0) {
                        px = bounds.right
                        points.add(Point(px, py))
                    } else if (newCheck and BoundsCheck.LEFT != 0) {
                        px = bounds.left
                        points.add(Point(px, py))
                    } else if (newCheck and BoundsCheck.BOTTOM != 0) {
                        if (rightPoint.x - bounds.x + newPoint.x - bounds.x < bounds.width) {
                            px = bounds.left
                        } else {
                            px = bounds.right
                        }
                        points.add(Point(px, py))
                        points.add(Point(px, bounds.bottom))
                    }
                } else if (rightCheck and BoundsCheck.BOTTOM != 0) {
                    py = bounds.bottom
                    if (newCheck and BoundsCheck.RIGHT != 0) {
                        px = bounds.right
                        points.add(Point(px, py))
                    } else if (newCheck and BoundsCheck.LEFT != 0) {
                        px = bounds.left
                        points.add(Point(px, py))
                    } else if (newCheck and BoundsCheck.TOP != 0) {
                        if (rightPoint.x - bounds.x + newPoint.x - bounds.x < bounds.width) {
                            px = bounds.left
                        } else {
                            px = bounds.right
                        }
                        points.add(Point(px, py))
                        points.add(Point(px, bounds.top))
                    }
                }
            }
            if (closingUp) {
                // newEdge's ends have already been added
                return
            }
            points.add(newPoint!!)
        }
        val newRightPoint = newEdge.clippedEnds!![LR.other(newOrientation)]
        if (!closeEnough(points[0], newRightPoint!!)) {
            points.add(newRightPoint!!)
        }
    }

    val _x: Double
        get() = coord!!.x

    val _y: Double
        get() = coord!!.y

    fun dist(p: ICoord): Double {
        return Point.distance(p.coord!!, this.coord!!)
    }

    companion object {

        private val EPSILON = .005
        private val pool = Stack<Site>()

        fun create(p: Point, index: Int, weight: Double, color: Color?): Site {
            if (pool.size > 0) {
                return pool.pop().init(p, index, weight, color)
            } else {
                return Site(p, index, weight, color)
            }
        }

        fun sortSites(sites: ArrayList<Site>) {
            //sites.sort(Site.compare);
            Collections.sort(sites) { o1, o2 -> Site.compare(o1, o2).toInt() }
        }

        /**
         * sort sites on y, then x, coord also change each site's _siteIndex to
         * match its new position in the list so the _siteIndex can be used to
         * identify the site for nearest-neighbor queries
         *
         *
         * haha "also" - means more than one responsibility...
         */
        private fun compare(s1: Site, s2: Site): Double {
            val returnValue = Voronoi.compareByYThenX(s1, s2)

            // swap _siteIndex values if necessary to match new ordering:
            val tempIndex: Int
            if (returnValue == -1) {
                if (s1._siteIndex > s2._siteIndex) {
                    tempIndex = s1._siteIndex
                    s1._siteIndex = s2._siteIndex
                    s2._siteIndex = tempIndex
                }
            } else if (returnValue == 1) {
                if (s2._siteIndex > s1._siteIndex) {
                    tempIndex = s2._siteIndex
                    s2._siteIndex = s1._siteIndex
                    s1._siteIndex = tempIndex
                }

            }

            return returnValue.toDouble()
        }

        private fun closeEnough(p0: Point, p1: Point): Boolean {
            return Point.distance(p0, p1) < EPSILON
        }
    }
}
