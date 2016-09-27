package xyz.yggdrazil.fortune.preview.gui.swing

import xyz.yggdrazil.fortune.Algorithm
import xyz.yggdrazil.fortune.AlgorithmWatcher
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent

class SweepControl(private val algorithm: Algorithm) : JComponent(), AlgorithmWatcher {

    private val ch = 20
    private val cw = 20

    init {
        preferredSize = Dimension(preferredSize.width, Companion.height)
        val controlMouseListener = ControlMouseAdapter()
        addMouseListener(controlMouseListener)
        addMouseMotionListener(controlMouseListener)
    }

    public override fun paintComponent(gr: Graphics) {
        val g = gr as Graphics2D

        g.color = Color.WHITE
        g.fillRect(0, 0, width, getHeight())

        g.color = Color.RED
        g.fillRect(Math.round(algorithm.sweepX).toInt() - cw / 2,
                getHeight() / 2 - ch / 2, cw, ch)
    }

    override fun update() {
        repaint()
    }

    private inner class ControlMouseAdapter : MouseAdapter() {
        private var pressed = false

        override fun mousePressed(e: MouseEvent?) {
            pressed = true
            val point = e!!.point
            algorithm.setSweep(point.x.toDouble())
        }

        override fun mouseReleased(e: MouseEvent?) {
            pressed = false
        }

        override fun mouseDragged(e: MouseEvent?) {
            if (pressed) {
                val point = e!!.point
                var x = point.x
                if (x < 0) {
                    x = 0
                }
                algorithm.setSweep(x.toDouble())
            }
        }
    }

    companion object {

        private val height = 30
    }
}
