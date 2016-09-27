package xyz.yggdrazil.fortune.preview.gui.swing

import xyz.yggdrazil.fortune.Algorithm
import xyz.yggdrazil.fortune.AlgorithmWatcher
import xyz.yggdrazil.fortune.geometry.Point
import xyz.yggdrazil.fortune.preview.gui.core.AlgorithmPainter
import xyz.yggdrazil.fortune.preview.gui.core.Config
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.JPanel

class Canvas(private val algorithm: Algorithm, config: Config) : JPanel(), AlgorithmWatcher {
    private val algorithmPainter: AlgorithmPainter
    private val painter: AwtPainter

    init {

        painter = AwtPainter()
        algorithmPainter = AlgorithmPainter(algorithm, config, painter)

        addMouseListener(object : MouseAdapter() {

            override fun mousePressed(e: MouseEvent?) {
                val point = Point(e!!.point.x.toDouble(), e.point.y.toDouble())
                if (point.x > this@Canvas.algorithm.sweepX) {
                    this@Canvas.algorithm.addSite(point)
                    repaint()
                }
            }

        })
    }

    override fun update() {
        repaint()
    }

    public override fun paintComponent(g: Graphics) {
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON)

        painter.graphics = g
        algorithmPainter.width = width
        algorithmPainter.height = height
        algorithmPainter.paint()
    }

    fun addRandomPoints() {
        val MARGIN = 20
        val MINSIZE = 20

        val sx = Math.ceil(algorithm.sweepX).toInt()
        val width = width - sx
        var marginX = 0
        var marginY = 0
        if (width >= MARGIN * 2 + MINSIZE) {
            marginX = MARGIN
        }
        if (height >= MARGIN * 2 + MINSIZE) {
            marginY = MARGIN
        }
        if (width <= 0) {
            return
        }
        val random = Random()
        for (i in 0..15) {
            val x = random.nextInt(width - marginX * 2 - 1) + sx + marginX + 1
            val y = random.nextInt(height - marginY * 2) + marginY
            algorithm.addSite(Point(x.toDouble(), y.toDouble()))
        }
        update()
    }

}
