package xyz.yggdrazil.gui.component

import xyz.yggdrazil.gui.model.Config
import xyz.yggdrazil.gui.model.voronoi.VoronoiDiagram
import xyz.yggdrazil.gui.render.VoronoiDiagramPainter
import xyz.yggdrazil.math.Point
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

/**
 * Created by Alexandre Mommers on 28/08/16.
 */

class Canvas constructor(private val config: Config,
                         private val model: VoronoiDiagram) : JPanel() {
    private val algorithmPainter: VoronoiDiagramPainter

    init {

        algorithmPainter = VoronoiDiagramPainter(model)
        addMouseListener(object : MouseAdapter() {

            override fun mousePressed(e: MouseEvent?) {
                val point = Point(e!!.point.x.toDouble(), e.point.y.toDouble())
                model.addSite(point)
                repaint()
            }

        })
    }

    public override fun paintComponent(graphics: Graphics) {
        if (graphics is Graphics2D) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON)
        }

        algorithmPainter.paintComponent(graphics, size)
    }

}
