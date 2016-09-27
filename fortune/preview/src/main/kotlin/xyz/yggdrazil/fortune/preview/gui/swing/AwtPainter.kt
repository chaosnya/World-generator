package xyz.yggdrazil.fortune.preview.gui.swing

import xyz.yggdrazil.fortune.preview.gui.core.Color
import xyz.yggdrazil.fortune.preview.gui.core.Coordinate
import xyz.yggdrazil.fortune.preview.gui.core.Painter
import java.awt.Graphics
import java.awt.Polygon

class AwtPainter(var graphics: Graphics? = null) : Painter {

    override fun setColor(color: Color) {
        graphics?.let {
            it.color = java.awt.Color(color.rgb)
        }
    }

    override fun fillRect(x: Int, y: Int, width: Int, height: Int) {
        graphics?.let {
            it.fillRect(x, y, width, height)
        }
    }

    override fun fillRect(x: Double, y: Double, width: Double, height: Double) {
        val ix = Math.round(x).toInt()
        val iy = Math.round(x).toInt()
        val w = Math.round(x + width - ix).toInt()
        val h = Math.round(y + height - iy).toInt()
        graphics?.let {
            it.fillRect(ix, iy, w, h)
        }
    }

    override fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
        graphics?.let {
            it.drawLine(x1, y1, x2, y2)
        }
    }

    override fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
        graphics?.let {
            it.drawLine(Math.round(x1).toInt(), Math.round(y1).toInt(),
                    Math.round(x2).toInt(), Math.round(y2).toInt())
        }
    }

    override fun drawPath(points: List<Coordinate>) {
        if (points.size < 2) {
            return
        }
        for (i in 0..points.size - 1 - 1) {
            val c1 = points[i]
            val c2 = points[i + 1]
            drawLine(c1.x, c1.y, c2.x, c2.y)
        }
    }

    override fun fillPath(points: List<Coordinate>) {
        graphics?.let {
            val polygon = Polygon()
            points.forEach { point ->
                polygon.addPoint(point.x.toInt(), point.y.toInt())
            }
            it.fillPolygon(polygon)
        }
    }

    override fun drawCircle(x: Double, y: Double, radius: Double) {
        val diam = radius * 2
        val d = Math.round(diam).toInt()
        val px = Math.round(x - radius).toInt()
        val py = Math.round(y - radius).toInt()
        graphics?.let {
            it.drawOval(px, py, d, d)
        }
    }

    override fun fillCircle(x: Double, y: Double, radius: Double) {
        val diam = radius * 2
        val d = Math.round(diam).toInt()
        val px = Math.round(x - radius).toInt()
        val py = Math.round(y - radius).toInt()
        graphics?.let {
            it.fillOval(px, py, d, d)
        }
    }

}
