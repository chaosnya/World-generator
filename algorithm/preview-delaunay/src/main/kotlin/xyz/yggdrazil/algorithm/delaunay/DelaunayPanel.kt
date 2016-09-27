package xyz.yggdrazil.algorithm.delaunay

import xyz.yggdrazil.math.Point
import xyz.yggdrazil.math.sub
import java.awt.Color
import java.awt.Graphics
import java.util.*
import javax.swing.JPanel

/**
 * Graphics Panel for DelaunayAp.
 */
class DelaunayPanel(private val controller: DelaunayAp) : JPanel() {
    private var dt: Triangulation? = null                   // Delaunay triangulation
    private val colorTable: MutableMap<Any, Color>      // Remembers colors for display
    private val initialTriangle: Triangle           // Initial triangle
    private var g: Graphics? = null                         // Stored graphics context
    private val random = Random()       // Source of random numbers

    init {
        initialTriangle = Triangle(
                Point(-initialSize, -initialSize),
                Point(initialSize, -initialSize),
                Point(0.0, initialSize))
        dt = Triangulation(initialTriangle)
        colorTable = HashMap<Any, Color>()
    }

    /**
     * Add a new site to the DT.

     * @param point the site to add
     */
    fun addSite(point: Point) {
        dt!!.delaunayPlace(point)
    }

    /**
     * Re-initialize the DT.
     */
    fun clear() {
        dt = Triangulation(initialTriangle)
    }

    /**
     * Get the color for the spcified item; generate a new color if necessary.

     * @param item we want the color for this item
     * *
     * @return item's color
     */
    private fun getColor(item: Any): Color {
        val color = colorTable[item]
        when (color) {
            is Color -> return color
            else -> {
                val color = DelaunayPanel.random()
                colorTable.put(item, color)
                return color
            }
        }
    }

    /* Basic Drawing Methods */

    /**
     * Draw a point.

     * @param point the Point to draw
     */
    fun draw(point: Point) {
        val r = pointRadius
        val x = point.coord(0).toInt()
        val y = point.coord(1).toInt()
        g!!.fillOval(x - r, y - r, r + r, r + r)
    }

    /**
     * Draw a circle.

     * @param center    the center of the circle
     * *
     * @param radius    the circle's radius
     * *
     * @param fillColor null implies no fill
     */
    fun draw(center: Point, radius: Double, fillColor: Color?) {
        val x = center.coord(0).toInt()
        val y = center.coord(1).toInt()
        val r = radius.toInt()
        if (fillColor != null) {
            val temp = g!!.color
            g!!.color = fillColor
            g!!.fillOval(x - r, y - r, r + r, r + r)
            g!!.color = temp
        }
        g!!.drawOval(x - r, y - r, r + r, r + r)
    }

    /**
     * Draw a polygon.

     * @param polygon   an array of polygon vertices
     * *
     * @param fillColor null implies no fill
     */
    fun draw(polygon: ArrayList<Point>, fillColor: Color?) {
        val x = IntArray(polygon.size)
        val y = IntArray(polygon.size)
        for (i in polygon.indices) {
            x[i] = polygon[i].coord(0).toInt()
            y[i] = polygon[i].coord(1).toInt()
        }
        if (fillColor != null) {
            val temp = g!!.color
            g!!.color = fillColor
            g!!.fillPolygon(x, y, polygon.size)
            g!!.color = temp
        }
        g!!.drawPolygon(x, y, polygon.size)
    }

    /* Higher Level Drawing Methods */

    /**
     * Handles painting entire contents of DelaunayPanel.
     * Called automatically; requested via call to repaint().

     * @param g the Graphics context
     */
    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        this.g = g

        // Flood the drawing area with a "background" color
        var temp = g.color
        if (!controller.isVoronoi)
            g.color = delaunayColor
        else if (dt!!.contains(initialTriangle))
            g.color = this.background
        else
            g.color = voronoiColor
        g.fillRect(0, 0, this.width, this.height)
        g.color = temp

        // If no colors then we can clear the color table
        if (!controller.isColorful) colorTable.clear()

        // Draw the appropriate picture
        if (controller.isVoronoi)
            drawAllVoronoi(controller.isColorful, true)
        else
            drawAllDelaunay(controller.isColorful)

        // Draw any extra info due to the mouse-entry switches
        temp = g.color
        g.color = Color.white
        if (controller.showingCircles()) drawAllCircles()
        if (controller.showingDelaunay()) drawAllDelaunay(false)
        if (controller.showingVoronoi()) drawAllVoronoi(false, false)
        g.color = temp
    }

    /**
     * Draw all the Delaunay triangles.

     * @param withFill true iff drawing Delaunay triangles with fill colors
     */
    fun drawAllDelaunay(withFill: Boolean) {
        for (triangle in dt!!) {
            val vertices = ArrayList<Point>()
            vertices.addAll(triangle.toTypedArray())
            draw(vertices, if (withFill) getColor(triangle) else null)
        }
    }

    /**
     * Draw all the Voronoi cells.

     * @param withFill  true iff drawing Voronoi cells with fill colors
     * *
     * @param withSites true iff drawing the site for each Voronoi cell
     */
    fun drawAllVoronoi(withFill: Boolean, withSites: Boolean) {
        // Keep track of sites done; no drawing for initial triangles sites
        val done = HashSet(initialTriangle)
        for (triangle in dt!!)
            for (site in triangle) {
                if (done.contains(site)) continue
                done.add(site)
                val list = dt!!.surroundingTriangles(site, triangle)
                val vertices = ArrayList<Point>(list.size)
                var i = 0
                for (tri in list)
                    vertices.add(tri.getCircumcenter())
                draw(vertices, if (withFill) getColor(site) else null)
                if (withSites) draw(site)
            }
    }

    /**
     * Draw all the empty circles (one for each triangle) of the DT.
     */
    fun drawAllCircles() {
        // Loop through all triangles of the DT
        for (triangle in dt!!) {
            // Skip circles involving the initial-triangle vertices
            if (triangle.containsAny(initialTriangle)) continue
            val c = triangle.getCircumcenter()
            val radius = c.sub(triangle[0]).magnitude()
            draw(c, radius, null)
        }
    }

    companion object {

        var voronoiColor = Color.magenta
        var delaunayColor = Color.green
        var pointRadius = 3
        private val initialSize = 10000.toDouble()     // Size of initial triangle

        fun random(): Color {
            val random = Random()
            return Color(Color.HSBtoRGB(random.nextFloat(), 1.0f, 1.0f))
        }
    }

}
