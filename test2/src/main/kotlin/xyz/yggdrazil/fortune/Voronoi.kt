package xyz.yggdrazil.fortune

/*
 * Java implementaition by Connor Clark (www.hotengames.com). Pretty much a 1:1
 * translation of a wonderful map generating algorthim by Amit Patel of Red Blob Games,
 * which can be found here (http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/)
 * Hopefully it's of use to someone out there who needed it in Java like I did!
 * Note, the only island mode implemented is Radial. Implementing more is something for another day.
 *
 * FORTUNE'S ALGORTIHIM
 *
 * This is a java implementation of an AS3 (Flash) implementation of an algorthim
 * originally created in C++. Pretty much a 1:1 translation from as3 to java, save
 * for some necessary workarounds. Original as3 implementation by Alan Shaw (of nodename)
 * can be found here (https://github.com/nodename/as3delaunay). Original algorthim
 * by Steven Fortune (see lisence for c++ implementation below)
 *
 * The author of this software is Steven Fortune.  Copyright (c) 1994 by AT&T
 * Bell Laboratories.
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY.  IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */

import xyz.yggdrazil.delaunay.geom.Point
import xyz.yggdrazil.delaunay.geom.Rectangle
import java.awt.Color
import java.util.*

class Voronoi {

    private var sites: SiteList? = null
    private var sitesIndexedByLocation: HashMap<Point, Site>? = null
    private var triangles: ArrayList<Triangle>? = null
    var edges: ArrayList<Edge>? = null
        private set

    // TODO generalize this so it doesn't have to be a rectangle;
    // then we can make the fractal voronois-within-voronois
    var plotBounds: Rectangle? = null
        private set

    fun dispose() {
        var i: Int
        var n: Int
        if (sites != null) {
            sites!!.dispose()
            sites = null
        }
        if (triangles != null) {
            n = triangles!!.size
            i = 0
            while (i < n) {
                triangles!![i].dispose()
                ++i
            }
            triangles!!.clear()
            triangles = null
        }
        if (edges != null) {
            n = edges!!.size
            i = 0
            while (i < n) {
                edges!![i].dispose()
                ++i
            }
            edges!!.clear()
            edges = null
        }
        plotBounds = null
        sitesIndexedByLocation = null
    }

    constructor(points: ArrayList<Point>, colors: ArrayList<Color>?, plotBounds: Rectangle) {
        init(points, colors, plotBounds)
        fortunesAlgorithm()
    }

    constructor(points: ArrayList<Point>, colors: ArrayList<Color>) {
        var maxWidth = 0.0
        var maxHeight = 0.0
        for (p in points) {
            maxWidth = Math.max(maxWidth, p.x)
            maxHeight = Math.max(maxHeight, p.y)
        }
        println("${maxWidth},${maxHeight}")
        init(points, colors, Rectangle(0.0, 0.0, maxWidth, maxHeight))
        fortunesAlgorithm()
    }

    constructor(numSites: Int, maxWidth: Double, maxHeight: Double, r: Random, colors: ArrayList<Color>) {
        val points = ArrayList<Point>()
        for (i in 0..numSites - 1) {
            points.add(Point(r.nextDouble() * maxWidth, r.nextDouble() * maxHeight))
        }
        init(points, colors, Rectangle(0.0, 0.0, maxWidth, maxHeight))
        fortunesAlgorithm()
    }

    private fun init(points: ArrayList<Point>, colors: ArrayList<Color>?, plotBounds: Rectangle) {
        sites = SiteList()
        sitesIndexedByLocation = HashMap()
        addSites(points, colors)
        this.plotBounds = plotBounds
        triangles = ArrayList()
        edges = ArrayList()
    }

    private fun addSites(points: ArrayList<Point>, colors: ArrayList<Color>?) {
        val length = points.size
        for (i in 0..length - 1) {
            addSite(points[i], null, i)
        }
    }

    private fun addSite(p: Point, color: Color?, index: Int) {
        val weight = Math.random() * 100
        val site = Site.create(p, index, weight, color)
        sites!!.push(site)
        sitesIndexedByLocation!!.put(p, site)
    }

    fun region(p: Point): ArrayList<Point> {
        val site = sitesIndexedByLocation!![p] ?: return ArrayList()
        return site.region(plotBounds!!)!!
    }

    // TODO: bug: if you call this before you call region(), something goes wrong :(
    fun neighborSitesForSite(coord: Point): ArrayList<Point> {
        val points = ArrayList<Point>()
        val site = sitesIndexedByLocation!![coord] ?: return points
        val sites = site.neighborSites()
        for (neighbor in sites) {
            points.add(neighbor!!.coord!!)
        }
        return points
    }

    fun circles(): ArrayList<Circle> {
        return sites!!.circles()
    }

    private fun selectEdgesForSitePoint(coord: Point, edgesToTest: ArrayList<Edge>): ArrayList<Edge> {
        val filtered = ArrayList<Edge>()

        for (e in edgesToTest) {
            if (e.leftSite != null && e.leftSite.coord == coord || e.rightSite != null && e.rightSite.coord == coord) {
                filtered.add(e)
            }
        }
        return filtered

        /*function myTest(edge:Edge, index:int, vector:Vector.<Edge>):Boolean
         {
         return ((edge.leftSite && edge.leftSite.coord == coord)
         ||  (edge.rightSite && edge.rightSite.coord == coord));
         }*/
    }

    private fun visibleLineSegments(edges: ArrayList<Edge>): ArrayList<LineSegment> {
        val segments = ArrayList<LineSegment>()

        for (edge in edges) {
            if (edge.visible) {
                val p1 = edge.clippedEnds!![LR.LEFT]
                val p2 = edge.clippedEnds!![LR.RIGHT]
                segments.add(LineSegment(p1, p2))
            }
        }

        return segments
    }

    private fun delaunayLinesForEdges(edges: ArrayList<Edge>): ArrayList<LineSegment> {
        val segments = ArrayList<LineSegment>()

        for (edge in edges) {
            segments.add(edge.delaunayLine())
        }

        return segments
    }

    fun voronoiBoundaryForSite(coord: Point): ArrayList<LineSegment> {
        return visibleLineSegments(selectEdgesForSitePoint(coord, edges!!))
    }

    fun delaunayLinesForSite(coord: Point): ArrayList<LineSegment> {
        return delaunayLinesForEdges(selectEdgesForSitePoint(coord, edges!!))
    }

    fun voronoiDiagram(): ArrayList<LineSegment> {
        return visibleLineSegments(edges!!)
    }

    /*public ArrayList<LineSegment> delaunayTriangulation(keepOutMask:BitmapData = null)
     {
     return delaunayLinesForEdges(selectNonIntersectingEdges(keepOutMask, edges));
     }*/
    fun hull(): ArrayList<LineSegment> {
        return delaunayLinesForEdges(hullEdges())
    }

    private fun hullEdges(): ArrayList<Edge> {
        val filtered = ArrayList<Edge>()

        for (e in edges!!) {
            if (e.isPartOfConvexHull) {
                filtered.add(e)
            }
        }


        return filtered

        /*function myTest(edge:Edge, index:int, vector:Vector.<Edge>):Boolean
         {
         return (edge.isPartOfConvexHull());
         }*/
    }

    fun hullPointsInOrder(): ArrayList<Point> {
        var hullEdges = hullEdges()

        val points = ArrayList<Point>()
        if (hullEdges.isEmpty()) {
            return points
        }

        val reorderer = EdgeReorderer(hullEdges, Site::class.java)
        hullEdges = reorderer.edges!!
        val orientations = reorderer.edgeOrientations
        reorderer.dispose()

        var orientation: LR

        val n = hullEdges.size
        for (i in 0..n - 1) {
            val edge = hullEdges[i]
            orientation = orientations!![i]
            points.add(edge.site(orientation).coord!!)
        }
        return points
    }

    fun regions(): ArrayList<ArrayList<Point>> {
        return sites!!.regions(plotBounds!!)
    }


    fun siteCoords(): ArrayList<Point> {
        return sites!!.siteCoords()
    }

    private fun fortunesAlgorithm() {
        var newSite: Site?
        var bottomSite: Site
        var topSite: Site
        var tempSite: Site
        var v: Vertex
        var vertex: Vertex
        var newintstar: Point? = null
        var leftRight: LR
        var lbnd: Halfedge
        var rbnd: Halfedge
        var llbnd: Halfedge
        var rrbnd: Halfedge
        var bisector: Halfedge
        var edge: Edge

        val dataBounds = sites!!.sitesBounds

        val sqrt_nsites = Math.sqrt(sites!!.length.toDouble() + 4).toInt()
        val heap = HalfedgePriorityQueue(dataBounds.y, dataBounds.height, sqrt_nsites)
        val edgeList = EdgeList(dataBounds.x, dataBounds.width, sqrt_nsites)
        val halfEdges = ArrayList<Halfedge>()
        val vertices = ArrayList<Vertex>()

        val bottomMostSite = sites!!.next()
        newSite = sites!!.next()

        while (true) {
            if (!heap.empty()) {
                newintstar = heap.min()
            }

            if (newSite != null && (heap.empty() || Companion.compareByYThenX(newSite, newintstar!!) < 0)) {
                /* new site is smallest */
                //trace("smallest: new site " + newSite);

                // Step 8:
                lbnd = edgeList.edgeListLeftNeighbor(newSite.coord!!)    // the Halfedge just to the left of newSite
                //trace("lbnd: " + lbnd);
                rbnd = lbnd.edgeListRightNeighbor!!        // the Halfedge just to the right
                //trace("rbnd: " + rbnd);
                bottomSite = rightRegion(lbnd, bottomMostSite!!)        // this is the same as leftRegion(rbnd)
                // this Site determines the region containing the new site
                //trace("new Site is in region of existing site: " + bottomSite);

                // Step 9:
                edge = Edge.createBisectingEdge(bottomSite, newSite)
                //trace("new edge: " + edge);
                edges!!.add(edge)

                bisector = Halfedge.create(edge, LR.LEFT)
                halfEdges.add(bisector)
                // inserting two Halfedges into edgeList constitutes Step 10:
                // insert bisector to the right of lbnd:
                edgeList.insert(lbnd, bisector)

                // first half of Step 11:
                Vertex.intersect(lbnd, bisector)?.let { vertex ->
                    vertices.add(vertex)
                    heap.remove(lbnd)
                    lbnd.vertex = vertex
                    lbnd.ystar = vertex._y + newSite!!.dist(vertex)
                    heap.insert(lbnd)
                }

                lbnd = bisector
                bisector = Halfedge.create(edge, LR.RIGHT)
                halfEdges.add(bisector)
                // second Halfedge for Step 10:
                // insert bisector to the right of lbnd:
                edgeList.insert(lbnd, bisector)

                // second half of Step 11:
                Vertex.intersect(bisector, rbnd)?.let { vertex ->
                    vertices.add(vertex)
                    bisector.vertex = vertex
                    bisector.ystar = vertex._y + newSite!!.dist(vertex)
                    heap.insert(bisector)
                }

                newSite = sites!!.next()
            } else if (!heap.empty()) {
                /* intersection is smallest */
                lbnd = heap.extractMin()
                llbnd = lbnd.edgeListLeftNeighbor!!
                rbnd = lbnd.edgeListRightNeighbor!!
                rrbnd = rbnd.edgeListRightNeighbor!!
                bottomSite = leftRegion(lbnd, bottomMostSite!!)
                topSite = rightRegion(rbnd, bottomMostSite)
                // these three sites define a Delaunay triangle
                // (not actually using these for anything...)
                //triangles.push(new Triangle(bottomSite, topSite, rightRegion(lbnd)));

                v = lbnd.vertex!!
                v.setIndex()
                lbnd.edge!!.setVertex(lbnd.leftRight!!, v)
                rbnd.edge!!.setVertex(rbnd.leftRight!!, v)
                edgeList.remove(lbnd)
                heap.remove(rbnd)
                edgeList.remove(rbnd)
                leftRight = LR.LEFT
                if (bottomSite._y > topSite._y) {
                    tempSite = bottomSite
                    bottomSite = topSite
                    topSite = tempSite
                    leftRight = LR.RIGHT
                }
                edge = Edge.createBisectingEdge(bottomSite, topSite)
                edges!!.add(edge)
                bisector = Halfedge.create(edge, leftRight)
                halfEdges.add(bisector)
                edgeList.insert(llbnd, bisector)
                edge.setVertex(LR.other(leftRight), v)
                Vertex.intersect(llbnd, bisector)?.let { vertex ->
                    vertices.add(vertex)
                    heap.remove(llbnd)
                    llbnd.vertex = vertex
                    llbnd.ystar = vertex._y + bottomSite.dist(vertex)
                    heap.insert(llbnd)
                }
                Vertex.intersect(bisector, rrbnd)?.let { vertex ->
                    vertices.add(vertex)
                    bisector.vertex = vertex
                    bisector.ystar = vertex._y + bottomSite.dist(vertex)
                    heap.insert(bisector)
                }
            } else {
                break
            }
        }

        // heap should be empty now
        heap.dispose()
        edgeList.dispose()

        for (halfEdge in halfEdges) {
            halfEdge.reallyDispose()
        }
        halfEdges.clear()

        // we need the vertices to clip the edges
        for (e in edges!!) {
            e.clipVertices(plotBounds!!)
        }
        // but we don't actually ever use them again!
        for (v0 in vertices) {
            v0.dispose()
        }
        vertices.clear()


    }

    internal fun leftRegion(he: Halfedge, bottomMostSite: Site): Site {
        val edge = he.edge ?: return bottomMostSite
        return edge.site(he.leftRight!!)
    }

    internal fun rightRegion(he: Halfedge, bottomMostSite: Site): Site {
        val edge = he.edge ?: return bottomMostSite
        return edge.site(LR.other(he.leftRight!!))
    }

    companion object {

        fun compareByYThenX(s1: Site, s2: Site): Int {
            if (s1._y < s2._y) {
                return -1
            }
            if (s1._y > s2._y) {
                return 1
            }
            if (s1._x < s2._x) {
                return -1
            }
            if (s1._x > s2._x) {
                return 1
            }
            return 0
        }

        fun compareByYThenX(s1: Site, s2: Point): Int {
            if (s1._y < s2.y) {
                return -1
            }
            if (s1._y > s2.y) {
                return 1
            }
            if (s1._x < s2.x) {
                return -1
            }
            if (s1._x > s2.x) {
                return 1
            }
            return 0
        }
    }
}
