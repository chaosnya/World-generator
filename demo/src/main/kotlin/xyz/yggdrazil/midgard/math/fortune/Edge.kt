package xyz.yggdrazil.midgard.math.fortune

import xyz.yggdrazil.midgard.math.geometry.Point
import xyz.yggdrazil.midgard.math.geometry.Rectangle
import java.util.*

/**
 * The line segment connecting the two Sites is part of the Delaunay
 * triangulation; the line segment connecting the two Vertices is part of the
 * Voronoi diagram

 * @author ashaw
 */
class Edge private constructor() {
    // the equation of the edge: ax + by = c
    var a: Double = 0.toDouble()
    var b: Double = 0.toDouble()
    var c: Double = 0.toDouble()
    // the two Voronoi vertices that the edge connects
    //		(if one of them is null, the edge extends to infinity)
    var leftVertex: Vertex? = null
        private set
    var rightVertex: Vertex? = null
        private set
    // Once clipVertices() is called, this Dictionary will hold two Points
    // representing the clipped coordinates of the left and right ends...
    var clippedEnds: HashMap<LR, Point>? = null
        private set

    // the two input Sites for which this Edge is a bisector:
    private var sites: HashMap<LR, Site>? = null

    // unless the entire Edge is outside the bounds.
    // In that case visible will be false:

    val visible: Boolean
        get() = clippedEnds != null

    var leftSite: Site
        get() = sites!![LR.LEFT]!!
        set(s) {
            sites!!.put(LR.LEFT, s)
        }

    var rightSite: Site
        get() = sites!![LR.RIGHT]!!
        set(s) {
            sites!!.put(LR.RIGHT, s)
        }

    private val edgeIndex: Int


    fun delaunayLine(): LineSegment {
        // draw a line connecting the input Sites for which the edge is a bisector:
        return LineSegment(leftSite.coord, rightSite.coord)
    }

    fun voronoiEdge(): LineSegment {
        if (!visible) {
            return LineSegment(null, null)
        }
        return LineSegment(clippedEnds!![LR.LEFT],
                clippedEnds!![LR.RIGHT])
    }

    fun setVertex(leftRight: LR, v: Vertex) {
        if (leftRight == LR.LEFT) {
            leftVertex = v
        } else {
            rightVertex = v
        }
    }

    val isPartOfConvexHull: Boolean
        get() = leftVertex == null || rightVertex == null

    fun sitesDistance(): Double {
        return Point.distance(leftSite.coord!!, rightSite.coord!!)
    }

    fun site(leftRight: LR): Site {
        return sites!![leftRight]!!
    }


    fun dispose() {

        leftVertex = null
        rightVertex = null
        if (clippedEnds != null) {
            clippedEnds!!.clear()
            clippedEnds = null
        }
        sites!!.clear()
        sites = null

        pool.push(this)
    }

    init {
        edgeIndex = nedges++
        init()
    }

    private fun init() {
        sites = HashMap<LR, Site>()
    }

    override fun toString(): String {
        return "Edge ${edgeIndex}; sites ${sites!![LR.LEFT]}, ${sites!![LR.RIGHT]}; endVertices ${leftVertex?.vertexIndex}, ${rightVertex?.vertexIndex}::"
    }

    /**
     * Set clippedVertices to contain the two ends of the portion of the
     * Voronoi edge that is visible within the bounds. If no part of the Edge
     * falls within the bounds, leave clippedVertices null.

     * @param bounds
     */
    fun clipVertices(bounds: Rectangle) {
        val xmin = bounds.x
        val ymin = bounds.y
        val xmax = bounds.right
        val ymax = bounds.bottom

        val vertex0: Vertex?
        val vertex1: Vertex?
        var x0: Double
        var x1: Double
        var y0: Double
        var y1: Double

        if (a == 1.0 && b >= 0.0) {
            vertex0 = rightVertex
            vertex1 = leftVertex
        } else {
            vertex0 = leftVertex
            vertex1 = rightVertex
        }

        if (a == 1.0) {
            y0 = ymin
            if (vertex0 != null && vertex0._y > ymin) {
                y0 = vertex0._y
            }
            if (y0 > ymax) {
                return
            }
            x0 = c - b * y0

            y1 = ymax
            if (vertex1 != null && vertex1._y < ymax) {
                y1 = vertex1._y
            }
            if (y1 < ymin) {
                return
            }
            x1 = c - b * y1

            if (x0 > xmax && x1 > xmax || x0 < xmin && x1 < xmin) {
                return
            }

            if (x0 > xmax) {
                x0 = xmax
                y0 = (c - x0) / b
            } else if (x0 < xmin) {
                x0 = xmin
                y0 = (c - x0) / b
            }

            if (x1 > xmax) {
                x1 = xmax
                y1 = (c - x1) / b
            } else if (x1 < xmin) {
                x1 = xmin
                y1 = (c - x1) / b
            }
        } else {
            x0 = xmin
            if (vertex0 != null && vertex0._x > xmin) {
                x0 = vertex0._x
            }
            if (x0 > xmax) {
                return
            }
            y0 = c - a * x0

            x1 = xmax
            if (vertex1 != null && vertex1._x < xmax) {
                x1 = vertex1._x
            }
            if (x1 < xmin) {
                return
            }
            y1 = c - a * x1

            if (y0 > ymax && y1 > ymax || y0 < ymin && y1 < ymin) {
                return
            }

            if (y0 > ymax) {
                y0 = ymax
                x0 = (c - y0) / a
            } else if (y0 < ymin) {
                y0 = ymin
                x0 = (c - y0) / a
            }

            if (y1 > ymax) {
                y1 = ymax
                x1 = (c - y1) / a
            } else if (y1 < ymin) {
                y1 = ymin
                x1 = (c - y1) / a
            }
        }

        clippedEnds = HashMap<LR, Point>()
        if (vertex0 == leftVertex) {
            clippedEnds!!.put(LR.LEFT, Point(x0, y0))
            clippedEnds!!.put(LR.RIGHT, Point(x1, y1))
        } else {
            clippedEnds!!.put(LR.RIGHT, Point(x0, y0))
            clippedEnds!!.put(LR.LEFT, Point(x1, y1))
        }
    }

    companion object {

        private val pool = Stack<Edge>()
        private var nedges = 0
        val DELETED = Edge()

        /**
         * This is the only way to create a new Edge

         * @param site0
         * *
         * @param site1
         * *
         * @return
         */
        fun createBisectingEdge(site0: Site, site1: Site): Edge {
            val dx: Double
            val dy: Double
            val absdx: Double
            val absdy: Double
            val a: Double
            val b: Double
            var c: Double

            dx = site1._x - site0._x
            dy = site1._y - site0._y
            absdx = if (dx > 0) dx else -dx
            absdy = if (dy > 0) dy else -dy
            c = site0._x * dx + site0._y * dy + (dx * dx + dy * dy) * 0.5
            if (absdx > absdy) {
                a = 1.0
                b = dy / dx
                c /= dx
            } else {
                b = 1.0
                a = dx / dy
                c /= dy
            }

            val edge = create()

            edge.leftSite = site0
            edge.rightSite = site1
            site0.addEdge(edge)
            site1.addEdge(edge)

            edge.leftVertex = null
            edge.rightVertex = null

            edge.a = a
            edge.b = b
            edge.c = c

            return edge
        }

        private fun create(): Edge {
            val edge: Edge
            if (pool.size > 0) {
                edge = pool.pop()
                edge.init()
            } else {
                edge = Edge()
            }
            return edge
        }

        fun compareSitesDistances_MAX(edge0: Edge, edge1: Edge): Double {
            val length0 = edge0.sitesDistance()
            val length1 = edge1.sitesDistance()
            if (length0 < length1) {
                return 1.0
            }
            if (length0 > length1) {
                return -1.0
            }
            return 0.0
        }

        fun compareSitesDistances(edge0: Edge, edge1: Edge): Double {
            return -compareSitesDistances_MAX(edge0, edge1)
        }
    }
}