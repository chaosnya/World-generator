package xyz.yggdrazil.fortune.preview.export.svg

import org.apache.batik.anim.dom.SVGDOMImplementation
import org.w3c.dom.Document
import org.w3c.dom.Element
import xyz.yggdrazil.fortune.preview.gui.core.Color
import xyz.yggdrazil.fortune.preview.gui.core.Coordinate
import xyz.yggdrazil.fortune.preview.gui.core.Painter
import java.util.*

class SvgPainter(private val doc: Document, private val root: Element) : Painter {

    private val svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI

    private var color: Color? = null

    override fun setColor(color: Color) {
        this.color = color
    }

    override fun fillRect(x: Int, y: Int, width: Int, height: Int) {
        val rectangle = doc.createElementNS(svgNS, "rect")
        rectangle.setAttributeNS(null, "x", Integer.toString(x))
        rectangle.setAttributeNS(null, "y", Integer.toString(y))
        rectangle.setAttributeNS(null, "width", Integer.toString(width))
        rectangle.setAttributeNS(null, "height", Integer.toString(height))
        rectangle.setAttributeNS(null, "fill", currentColor)

        root.appendChild(rectangle)
    }

    override fun fillRect(x: Double, y: Double, width: Double, height: Double) {
        val rectangle = doc.createElementNS(svgNS, "rect")
        rectangle.setAttributeNS(null, "x", java.lang.Double.toString(x))
        rectangle.setAttributeNS(null, "y", java.lang.Double.toString(y))
        rectangle.setAttributeNS(null, "width", java.lang.Double.toString(width))
        rectangle.setAttributeNS(null, "height", java.lang.Double.toString(height))
        rectangle.setAttributeNS(null, "fill", currentColor)

        root.appendChild(rectangle)
    }

    override fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
        drawLine(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())
    }

    override fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
        val path = doc.createElementNS(svgNS, "path")
        path.setAttributeNS(
                null,
                "style",
                "fill:none;stroke:"
                        + currentColor
                        + ";stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1")
        path.setAttributeNS(null, "d",
                String.format(Locale.US, "M %f,%f %f,%f", x1, y1, x2, y2))

        root.appendChild(path)
    }

    override fun drawPath(points: List<Coordinate>) {
        if (points.size < 2) {
            return
        }

        val strb = StringBuilder()
        val start = points[0]
        strb.append(String.format(Locale.US, "M %f,%f", start.x,
                start.y))

        for (i in 1..points.size - 1) {
            val c = points[i]
            strb.append(String.format(Locale.US, " %f,%f", c.x, c.y))
        }

        val path = doc.createElementNS(svgNS, "path")
        path.setAttributeNS(
                null,
                "style",
                "fill:none;stroke:"
                        + currentColor
                        + ";stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1")
        path.setAttributeNS(null, "d", strb.toString())

        root.appendChild(path)
    }

    override fun fillPath(points: List<Coordinate>) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun drawCircle(x: Double, y: Double, radius: Double) {
        val circle = doc.createElementNS(svgNS, "circle")
        circle.setAttributeNS(null, "cx", java.lang.Double.toString(x))
        circle.setAttributeNS(null, "cy", java.lang.Double.toString(y))
        circle.setAttributeNS(null, "r", java.lang.Double.toString(radius))
        circle.setAttributeNS(null, "fill", "none")
        circle.setAttributeNS(null, "stroke", currentColor)
        circle.setAttributeNS(null, "stroke-width", "1")

        root.appendChild(circle)
    }

    override fun fillCircle(x: Double, y: Double, radius: Double) {
        val circle = doc.createElementNS(svgNS, "circle")
        circle.setAttributeNS(null, "cx", java.lang.Double.toString(x))
        circle.setAttributeNS(null, "cy", java.lang.Double.toString(y))
        circle.setAttributeNS(null, "r", java.lang.Double.toString(radius))
        circle.setAttributeNS(null, "fill", currentColor)

        root.appendChild(circle)
    }

    private val currentColor: String
        get() = String.format("#%06x", color!!.rgb)

}
