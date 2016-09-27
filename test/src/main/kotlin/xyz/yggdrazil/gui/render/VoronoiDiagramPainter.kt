package xyz.yggdrazil.gui.render

import xyz.yggdrazil.gui.model.voronoi.VoronoiDiagram
import xyz.yggdrazil.helpers.Color
import xyz.yggdrazil.math.Point
import xyz.yggdrazil.math.Vector
import java.awt.Dimension
import java.awt.Graphics
import java.util.*

/**
 * Created by Alexandre Mommers on 28/08/16.
 */
class VoronoiDiagramPainter(private val model: VoronoiDiagram) {

    // Remembers colors for display
    private val colorTable = HashMap<Any, java.awt.Color>()

    // Stored graphics context
    private lateinit var graphics: Graphics
    private lateinit var dimension: Dimension


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
                Color.random().let {
                    colorTable.put(item, it)
                }
                return getColor(item)
            }
        }
    }

    /* Basic Drawing Methods */

    /**
     * Draw a point.

     * @param point the Point to draw
     */
    fun draw(point: Point) {
        val r = 2
        val x = point.x * dimension.width / 100.0
        val y = point.y * dimension.height / 100.0
        graphics.fillOval(x.toInt() - r, y.toInt() - r, r + r, r + r)
    }

    /**
     * Draw a polygon.

     * @param polygon   an array of polygon vertices
     * *
     * @param fillColor null implies no fill
     */
    fun draw(polygon: Array<Vector>, fillColor: Color?) {
        val x = IntArray(polygon.size)
        val y = IntArray(polygon.size)
        for (i in polygon.indices) {
            x[i] = (polygon[i].x * dimension.width / 100.0).toInt()
            y[i] = (polygon[i].y * dimension.height / 100.0).toInt()
        }
        if (fillColor is Color) {
            val temp = graphics.color
            graphics.color = fillColor
            graphics.fillPolygon(x, y, polygon.size)
            graphics.color = temp
        }
        graphics.drawPolygon(x, y, polygon.size)
    }

    /* Higher Level Drawing Methods */

    /**
     * Handles painting entire contents of DelaunayPanel.
     * Called automatically; requested via call to repaint().

     * @param g the Graphics context
     */
    fun paintComponent(graphics: Graphics, dimension: Dimension) {
        this.graphics = graphics
        this.dimension = dimension

        // Draw the appropriate picture
        drawAllVoronoi()

        // Draw any extra info due to the mouse-entry switches
        graphics.color = java.awt.Color.white
        //drawAllDelaunay()
    }

    /**
     * Draw all the Delaunay triangles.

     * @param withFill true iff drawing Delaunay triangles with fill colors
     *
    fun drawAllDelaunay() {
    for (triangle in model.delaunayTriangulation) {
    val vertices = triangle.toTypedArray()
    draw(vertices, null)
    }
    }*/

    /**
     * Draw all the Voronoi cells.

     * @param withFill  true iff drawing Voronoi cells with fill colors
     * *
     * @param withSites true iff drawing the site for each Voronoi cell
     */
    fun drawAllVoronoi() {
        // Keep track of sites done; no drawing for initial triangles sites

        for (polygon in model.polygons) {
            draw(polygon.toTypedArray(), getColor(polygon))
            draw(polygon.origin)
        }
    }

}
