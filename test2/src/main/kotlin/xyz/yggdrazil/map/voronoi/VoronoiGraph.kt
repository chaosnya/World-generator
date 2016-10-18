package xyz.yggdrazil.map.voronoi

import xyz.yggdrazil.math.geometry.Point
import xyz.yggdrazil.math.geometry.Rectangle
import xyz.yggdrazil.fortune.Voronoi
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.*

/**
 * VoronoiGraph.java

 * @author Connor
 */
abstract class VoronoiGraph(var voronoi: Voronoi, numLloydRelaxations: Int, private val r: Random) {

    val edges = ArrayList<Edge>()
    val corners = ArrayList<Corner>()
    val centers = ArrayList<Center>()
    val bounds: Rectangle
    val pixelCenterMap: BufferedImage
    internal val bumps: Int
    internal val startAngle: Double
    internal val dipAngle: Double
    internal val dipWidth: Double
    protected var OCEAN: Color? = null
    protected var RIVER: Color? = null
    protected var LAKE: Color? = null
    protected var BEACH: Color? = null
    internal var ISLAND_FACTOR = 2.07  // 1.0 means no small islands; 2.0 leads to a lot

    init {
        bumps = r.nextInt(5) + 1
        startAngle = r.nextDouble() * 2.0 * Math.PI
        dipAngle = r.nextDouble() * 2.0 * Math.PI
        dipWidth = r.nextDouble() * .5 + .2
        bounds = voronoi.plotBounds
        for (i in 0..numLloydRelaxations - 1) {
            val points = voronoi.siteCoords()
            for (p in points) {
                val region = voronoi.region(p)
                var x = 0.0
                var y = 0.0
                for (c in region) {
                    x += c.x
                    y += c.y
                }
                x /= region.size.toDouble()
                y /= region.size.toDouble()
                p.x = x
                p.y = y
            }
            voronoi = Voronoi(points, voronoi.plotBounds)
        }
        buildGraph(voronoi)
        improveCorners()

        assignCornerElevations()
        assignOceanCoastAndLand()
        redistributeElevations(landCorners())
        assignPolygonElevations()

        calculateDownslopes()
        //calculateWatersheds();
        createRivers()
        assignCornerMoisture()
        redistributeMoisture(landCorners())
        assignPolygonMoisture()
        assignBiomes()

        pixelCenterMap = BufferedImage(bounds.width.toInt(), bounds.width.toInt(), BufferedImage.TYPE_4BYTE_ABGR)
    }

    protected abstract fun getBiome(p: Center): Enum<*>

    protected abstract fun getColor(biome: Enum<*>): Color

    private fun improveCorners() {
        val newP = arrayOfNulls<Point>(corners.size)
        for (c in corners) {
            if (c.border) {
                newP[c.index] = c.loc
            } else {
                var x = 0.0
                var y = 0.0
                for (center in c.touches) {
                    x += center.loc.x
                    y += center.loc.y
                }
                newP[c.index] = Point(x / c.touches.size, y / c.touches.size)
            }
        }
        corners.forEach { c -> c.loc = newP[c.index] }
        edges.filter({ e -> e.v0 != null && e.v1 != null }).forEach { e -> e.setVornoi(e.v0!!, e.v1!!) }
    }

    private fun edgeWithCenters(c1: Center, c2: Center): Edge? {
        for (e in c1.borders) {
            if (e.d0 == c2 || e.d1 == c2) {
                return e
            }
        }
        return null
    }

    private fun drawTriangle(g: Graphics2D, c1: Corner, c2: Corner, center: Center) {
        val x = IntArray(3)
        val y = IntArray(3)
        x[0] = center.loc.x.toInt()
        y[0] = center.loc.y.toInt()
        x[1] = c1.loc!!.x.toInt()
        y[1] = c1.loc!!.y.toInt()
        x[2] = c2.loc!!.x.toInt()
        y[2] = c2.loc!!.y.toInt()
        g.fillPolygon(x, y, 3)
    }

    private fun closeEnough(d1: Double, d2: Double, diff: Double): Boolean {
        return Math.abs(d1 - d2) <= diff
    }

    fun createMap(): BufferedImage {
        val size = bounds.width.toInt()

        val img = BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR)
        val g = img.createGraphics()

        paint(g)

        return img
    }

    private fun drawPolygon(g: Graphics2D, c: Center, color: Color) {
        g.color = color

        //only used if Center c is on the edge of the graph. allows for completely filling in the outer polygons
        var edgeCorner1: Corner? = null
        var edgeCorner2: Corner? = null
        c.area = 0.0
        for (n in c.neighbors) {
            val e = edgeWithCenters(c, n) ?: continue

            //outermost voronoi edges aren't stored in the graph
            val v0 = e.v0 ?: continue
            val v1 = e.v1 ?: continue

            //find a corner on the exterior of the graph
            //if this Edge e has one, then it must have two,
            //finding these two corners will give us the missing
            //triangle to render. this special triangle is handled
            //outside this for loop
            val cornerWithOneAdjacent = if (v0.border) v0 else v1

            if (cornerWithOneAdjacent.border) {
                if (edgeCorner1 == null) {
                    edgeCorner1 = cornerWithOneAdjacent
                } else {
                    edgeCorner2 = cornerWithOneAdjacent
                }
            }

            drawTriangle(g, v0, v1, c)
            c.area = c.area + Math.abs(c.loc.x * (e.v0!!.loc!!.y - e.v1!!.loc!!.y)
                    + e.v0!!.loc!!.x * (e.v1!!.loc!!.y - c.loc.y)
                    + e.v1!!.loc!!.x * (c.loc.y - e.v0!!.loc!!.y)) / 2
        }

        //handle the missing triangle
        if (edgeCorner2 != null) {
            //if these two outer corners are NOT on the same exterior edge of the graph,
            //then we actually must render a polygon (w/ 4 points) and take into consideration
            //one of the four corners (either 0,0 or 0,height or width,0 or width,height)
            //note: the 'missing polygon' may have more than just 4 points. this
            //is common when the number of sites are quite low (less than 5), but not a problem
            //with a more useful number of sites.
            //TODO: find a way to fix this

            if (closeEnough(edgeCorner1!!.loc!!.x, edgeCorner2.loc!!.x, 1.0)) {
                drawTriangle(g, edgeCorner1, edgeCorner2, c)
            } else {
                val x = IntArray(4)
                val y = IntArray(4)
                x[0] = c.loc.x.toInt()
                y[0] = c.loc.y.toInt()
                x[1] = edgeCorner1.loc!!.x.toInt()
                y[1] = edgeCorner1.loc!!.y.toInt()

                //determine which corner this is
                x[2] = (if (closeEnough(edgeCorner1.loc!!.x, bounds.x, 1.0) || closeEnough(edgeCorner2.loc!!.x, bounds.x, .5)) bounds.x else bounds.right).toInt()
                y[2] = (if (closeEnough(edgeCorner1.loc!!.y, bounds.y, 1.0) || closeEnough(edgeCorner2.loc!!.y, bounds.y, .5)) bounds.y else bounds.bottom).toInt()

                x[3] = edgeCorner2.loc!!.x.toInt()
                y[3] = edgeCorner2.loc!!.y.toInt()

                g.fillPolygon(x, y, 4)
                c.area = c.area + 0 //TODO: area of polygon given vertices
            }
        }
    }

    //also records the area of each voronoi cell
    @JvmOverloads fun paint(g: Graphics2D, drawBiomes: Boolean = true, drawRivers: Boolean = true, drawSites: Boolean = false, drawCorners: Boolean = false, drawDelaunay: Boolean = false) {
        val numSites = centers.size

        var defaultColors = ArrayList<Color>(numSites)
        if (!drawBiomes) {
            for (i in defaultColors.indices) {
                defaultColors.add(Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)))
            }
        }

        val pixelCenterGraphics = pixelCenterMap.createGraphics()

        //draw via triangles
        for (c in centers) {
            drawPolygon(g, c, if (drawBiomes) getColor(c.biome!!) else defaultColors[c.index])
            drawPolygon(pixelCenterGraphics, c, Color(c.index))
        }

        for (e in edges) {
            if (drawDelaunay) {
                g.stroke = BasicStroke(1f)
                g.color = Color.YELLOW
                g.drawLine(e.d0!!.loc.x.toInt(), e.d0!!.loc.y.toInt(), e.d1!!.loc.x.toInt(), e.d1!!.loc.y.toInt())
            }
            if (drawRivers && e.river > 0) {
                g.stroke = BasicStroke((1 + Math.sqrt((e.river * 2).toDouble()).toInt()).toFloat())
                g.color = RIVER
                g.drawLine(e.v0!!.loc!!.x.toInt(), e.v0!!.loc!!.y.toInt(), e.v1!!.loc!!.x.toInt(), e.v1!!.loc!!.y.toInt())
            }
        }

        if (drawSites) {
            g.color = Color.BLACK
            centers.forEach { s -> g.fillOval((s.loc.x - 2).toInt(), (s.loc.y - 2).toInt(), 4, 4) }
        }

        if (drawCorners) {
            g.color = Color.WHITE
            corners.forEach { c -> g.fillOval((c.loc!!.x - 2).toInt(), (c.loc!!.y - 2).toInt(), 4, 4) }
        }
        g.color = Color.WHITE
        g.drawRect(bounds.x.toInt(), bounds.y.toInt(), bounds.width.toInt(), bounds.height.toInt())
    }

    private fun buildGraph(v: Voronoi) {
        val pointCenterMap = HashMap<Point, Center>()
        val points = v.siteCoords()
        points.forEach { p ->
            val c = Center(p)
            c.index = centers.size
            centers.add(c)
            pointCenterMap.put(p, c)
        }

        //bug fix
        centers.forEach { c -> v.region(c.loc) }

        val libedges = v.edges
        val pointCornerMap = HashMap<Int, Corner>()

        for (libedge in libedges!!) {
            val vEdge = libedge.voronoiEdge()
            val dEdge = libedge.delaunayLine()

            val edge = Edge()
            edge.index = edges.size
            edges.add(edge)

            edge.v0 = makeCorner(pointCornerMap, vEdge.p0)
            edge.v1 = makeCorner(pointCornerMap, vEdge.p1)
            edge.d0 = pointCenterMap.get(dEdge.p0)
            edge.d1 = pointCenterMap.get(dEdge.p1)

            // Centers point to edges. Corners point to edges.
            if (edge.d0 != null) {
                edge.d0!!.borders.add(edge)
            }
            if (edge.d1 != null) {
                edge.d1!!.borders.add(edge)
            }
            if (edge.v0 != null) {
                edge.v0!!.protrudes.add(edge)
            }
            if (edge.v1 != null) {
                edge.v1!!.protrudes.add(edge)
            }

            // Centers point to centers.
            if (edge.d0 != null && edge.d1 != null) {
                addToCenterList(edge.d0!!.neighbors, edge.d1)
                addToCenterList(edge.d1!!.neighbors, edge.d0)
            }

            // Corners point to corners
            if (edge.v0 != null && edge.v1 != null) {
                addToCornerList(edge.v0!!.adjacent, edge.v1)
                addToCornerList(edge.v1!!.adjacent, edge.v0)
            }

            // Centers point to corners
            if (edge.d0 != null) {
                addToCornerList(edge.d0!!.corners, edge.v0)
                addToCornerList(edge.d0!!.corners, edge.v1)
            }
            if (edge.d1 != null) {
                addToCornerList(edge.d1!!.corners, edge.v0)
                addToCornerList(edge.d1!!.corners, edge.v1)
            }

            // Corners point to centers
            if (edge.v0 != null) {
                addToCenterList(edge.v0!!.touches, edge.d0)
                addToCenterList(edge.v0!!.touches, edge.d1)
            }
            if (edge.v1 != null) {
                addToCenterList(edge.v1!!.touches, edge.d0)
                addToCenterList(edge.v1!!.touches, edge.d1)
            }
        }
    }

    // Helper functions for the following for loop; ideally these
    // would be inlined
    private fun addToCornerList(list: ArrayList<Corner>, c: Corner?) {
        if (c != null && !list.contains(c)) {
            list.add(c)
        }
    }

    private fun addToCenterList(list: ArrayList<Center>, c: Center?) {
        if (c != null && !list.contains(c)) {
            list.add(c)
        }
    }

    //ensures that each corner is represented by only one corner object
    private fun makeCorner(pointCornerMap: HashMap<Int, Corner>, p: Point?): Corner? {
        if (p == null) {
            return null
        }
        val index = (p.x.toInt() + p.y.toInt().toDouble() * bounds.width * 2.0).toInt()
        var c: Corner? = pointCornerMap[index]
        if (c == null) {
            c = Corner()
            c.loc = p
            c.border = bounds.liesOnAxes(p)
            c.index = corners.size
            corners.add(c)
            pointCornerMap.put(index, c)
        }
        return c
    }

    private fun assignCornerElevations() {
        val queue = LinkedList<Corner>()
        for (c in corners) {
            c.water = isWater(c.loc!!)
            if (c.border) {
                c.elevation = 0.0
                queue.add(c)
            } else {
                c.elevation = java.lang.Double.MAX_VALUE
            }
        }

        while (!queue.isEmpty()) {
            val c = queue.pop()
            for (a in c.adjacent) {
                var newElevation = 0.01 + c.elevation
                if (!c.water && !a.water) {
                    newElevation += 1.0
                }
                if (newElevation < a.elevation) {
                    a.elevation = newElevation
                    queue.add(a)
                }
            }
        }
    }

    //only the radial implementation of amitp's map generation
    //TODO implement more island shapes
    private fun isWater(p: Point): Boolean {
        var p = p
        p = Point(2 * (p.x / bounds.width - 0.5), 2 * (p.y / bounds.height - 0.5))

        val angle = Math.atan2(p.y, p.x)
        val length = 0.5 * (Math.max(Math.abs(p.x), Math.abs(p.y)) + p.length())

        var r1 = 0.5 + 0.40 * Math.sin(startAngle + bumps * angle + Math.cos((bumps + 3) * angle))
        var r2 = 0.7 - 0.20 * Math.sin(startAngle + bumps * angle - Math.sin((bumps + 2) * angle))
        if (Math.abs(angle - dipAngle) < dipWidth
                || Math.abs(angle - dipAngle + 2 * Math.PI) < dipWidth
                || Math.abs(angle - dipAngle - 2 * Math.PI) < dipWidth) {
            r1 = 0.2
            r2 = 0.2
        }
        return !(length < r1 || length > r1 * ISLAND_FACTOR && length < r2)

        //return false;

        /*if (noise == null) {
         noise = new Perlin2d(.125, 8, MyRandom.seed).createArray(257, 257);
         }
         int x = (int) ((p.x + 1) * 128);
         int y = (int) ((p.y + 1) * 128);
         return noise[x][y] < .3 + .3 * p.l2();*/

        /*boolean eye1 = new Point(p.x - 0.2, p.y / 2 + 0.2).length() < 0.05;
         boolean eye2 = new Point(p.x + 0.2, p.y / 2 + 0.2).length() < 0.05;
         boolean body = p.length() < 0.8 - 0.18 * Math.sin(5 * Math.atan2(p.y, p.x));
         return !(body && !eye1 && !eye2);*/
    }

    private fun assignOceanCoastAndLand() {
        val queue = LinkedList<Center>()
        val waterThreshold = .3
        for (center in centers) {
            var numWater = 0
            for (c in center.corners) {
                if (c.border) {
                    center.ocean = true
                    center.water = true
                    center.border = true
                    queue.add(center)
                }
                if (c.water) {
                    numWater++
                }
            }
            center.water = center.ocean || numWater.toDouble() / center.corners.size >= waterThreshold
        }
        while (!queue.isEmpty()) {
            val center = queue.pop()
            for (n in center.neighbors) {
                if (n.water && !n.ocean) {
                    n.ocean = true
                    queue.add(n)
                }
            }
        }
        for (center in centers) {
            var oceanNeighbor = false
            var landNeighbor = false
            for (n in center.neighbors) {
                oceanNeighbor = oceanNeighbor or n.ocean
                landNeighbor = landNeighbor or !n.water
            }
            center.coast = oceanNeighbor && landNeighbor
        }

        for (c in corners) {
            var numOcean = 0
            var numLand = 0
            for (center in c.touches) {
                numOcean += if (center.ocean) 1 else 0
                numLand += if (!center.water) 1 else 0
            }
            c.ocean = numOcean == c.touches.size
            c.coast = numOcean > 0 && numLand > 0
            c.water = c.border || numLand != c.touches.size && !c.coast
        }
    }

    private fun landCorners(): ArrayList<Corner> {
        val list = ArrayList<Corner>()
        for (c in corners) {
            if (!c.ocean && !c.coast) {
                list.add(c)
            }
        }
        return list
    }

    private fun redistributeElevations(landCorners: ArrayList<Corner>) {
        Collections.sort(landCorners, Comparator<xyz.yggdrazil.map.voronoi.Corner> { o1, o2 ->
            if (o1.elevation > o2.elevation) {
                return@Comparator 1
            } else if (o1.elevation < o2.elevation) {
                return@Comparator -1
            }
            0
        })

        val SCALE_FACTOR = 1.1
        for (i in landCorners.indices) {
            val y = i.toDouble() / landCorners.size
            var x = Math.sqrt(SCALE_FACTOR) - Math.sqrt(SCALE_FACTOR * (1 - y))
            x = Math.min(x, 1.0)
            landCorners[i].elevation = x
        }

        for (c in corners) {
            if (c.ocean || c.coast) {
                c.elevation = 0.0
            }
        }
    }

    private fun assignPolygonElevations() {
        for (center in centers) {
            var total = 0.0
            for (c in center.corners) {
                total += c.elevation
            }
            center.elevation = total / center.corners.size
        }
    }

    private fun calculateDownslopes() {
        for (c in corners) {
            var down = c
            for (a in c.adjacent) {
                if (a.elevation <= down.elevation) {
                    down = a
                }
            }
            c.downslope = down
        }
    }

    private fun createRivers() {
        var i = 0
        while (i < bounds.width / 2) {
            var c = corners[r.nextInt(corners.size)]
            if (c.ocean || c.elevation < 0.3 || c.elevation > 0.9) {
                i++
                continue
            }
            // Bias rivers to go west: if (q.downslope.x > q.x) continue;
            while (!c.coast) {
                if (c == c.downslope) {
                    break
                }
                val edge = lookupEdgeFromCorner(c, c.downslope!!)
                if (!edge!!.v0!!.water || !edge.v1!!.water) {
                    edge.river = edge.river + 1
                    c.river = c.river + 1
                    c.downslope!!.river = c.downslope!!.river + 1  // TODO: fix double count
                }
                c = c.downslope!!
            }
            i++
        }
    }

    private fun lookupEdgeFromCorner(c: Corner, downslope: Corner): Edge? {
        for (e in c.protrudes) {
            if (e.v0 == downslope || e.v1 == downslope) {
                return e
            }
        }
        return null
    }

    private fun assignCornerMoisture() {
        val queue = LinkedList<Corner>()
        for (c in corners) {
            if ((c.water || c.river > 0) && !c.ocean) {
                c.moisture = if (c.river > 0) Math.min(3.0, 0.2 * c.river) else 1.0
                queue.push(c)
            } else {
                c.moisture = 0.0
            }
        }

        while (!queue.isEmpty()) {
            val c = queue.pop()
            for (a in c.adjacent) {
                val newM = .9 * c.moisture
                if (newM > a.moisture) {
                    a.moisture = newM
                    queue.add(a)
                }
            }
        }

        // Salt water
        for (c in corners) {
            if (c.ocean || c.coast) {
                c.moisture = 1.0
            }
        }
    }

    private fun redistributeMoisture(landCorners: ArrayList<Corner>) {
        Collections.sort(landCorners, Comparator<xyz.yggdrazil.map.voronoi.Corner> { o1, o2 ->
            if (o1.moisture > o2.moisture) {
                return@Comparator 1
            } else if (o1.moisture < o2.moisture) {
                return@Comparator -1
            }
            0
        })
        for (i in landCorners.indices) {
            landCorners[i].moisture = i.toDouble() / landCorners.size
        }
    }

    private fun assignPolygonMoisture() {
        for (center in centers) {
            var total = 0.0
            for (c in center.corners) {
                total += c.moisture
            }
            center.moisture = total / center.corners.size
        }
    }

    private fun assignBiomes() {
        for (center in centers) {
            center.biome = getBiome(center)
        }
    }
}
