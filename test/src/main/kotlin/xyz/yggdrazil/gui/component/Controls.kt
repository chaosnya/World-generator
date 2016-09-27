package xyz.yggdrazil.gui.component

import xyz.yggdrazil.gui.model.voronoi.VoronoiDiagram
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel

class Controls(val canvas: Canvas, model: VoronoiDiagram) : Box(BoxLayout.Y_AXIS) {

    init {

        addSection().addButton("reset") {
            update {
                model.reset()
            }
        }

        addSection().addText("Points")

        addSection().let { layout ->

            layout.addButton("increase") {
                update {
                    model.addRandomPoints()
                }
            }

            layout.addButton("reduce") {
                update {
                    model.removeRandomPoint()
                }
            }
        }

        addSection().addText("Centroid")

        addSection().let { layout ->

            layout.addButton("increase") {
                update {
                    model.increaseCentroid()
                }
            }

            layout.addButton("reduce") {
                update {
                    model.reduceCentroid()
                }
            }
        }

    }

    fun update(action: () -> Unit) {

        action()

        canvas.repaint()

    }

    fun addSection(): Box {
        val box = Box(BoxLayout.X_AXIS)
        add(box)
        return box
    }

    fun Box.addButton(text: String, action: () -> Unit) {
        JButton(text).let { button ->
            add(button)
            button.addActionListener {
                action()
            }
        }
    }

    fun Box.addText(text: String) {
        JLabel(text).let {
            add(it)
        }
    }
}