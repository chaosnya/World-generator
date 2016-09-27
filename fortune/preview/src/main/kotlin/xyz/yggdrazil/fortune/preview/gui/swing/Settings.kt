package xyz.yggdrazil.fortune.preview.gui.swing

import xyz.yggdrazil.fortune.preview.gui.core.Config
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.JButton
import javax.swing.JToggleButton
import javax.swing.JToolBar

class Settings(private val canvas: Canvas, private val config: Config) : JToolBar(), ItemListener {

    init {

        isFloatable = false

        val `as` = arrayOf(TEXT_CIRCLES, TEXT_BEACHLINE, TEXT_VORONOI, TEXT_DELAUNAY)

        val buttons = arrayListOf<JToggleButton>()
        for (i in `as`.indices) {
            buttons.add(JToggleButton(`as`[i]))
            buttons[i].addItemListener(this)
            add(buttons[i])
        }

        buttons[0].isSelected = config.isDrawCircles
        buttons[1].isSelected = config.isDrawBeach
        buttons[2].isSelected = config.isDrawVoronoiLines
        buttons[3].isSelected = config.isDrawDelaunay

        val buttonRandom = JButton(TEXT_ADD_RANDOM)
        add(buttonRandom)

        buttonRandom.addActionListener {
            this.canvas.addRandomPoints()
        }
    }

    override fun itemStateChanged(e: ItemEvent) {
        val button = e.item as JToggleButton
        val s = button.text
        val flag = button.isSelected
        if (s == TEXT_CIRCLES) {
            config.isDrawCircles = flag
        } else if (s == TEXT_BEACHLINE) {
            config.isDrawBeach = flag
        } else if (s == TEXT_VORONOI) {
            config.isDrawVoronoiLines = flag
        } else if (s == TEXT_DELAUNAY) {
            config.isDrawDelaunay = flag
        }
        canvas.repaint()
    }

    companion object {

        private val TEXT_CIRCLES = "Circles"
        private val TEXT_BEACHLINE = "Beachline"
        private val TEXT_VORONOI = "Voronoi diagram"
        private val TEXT_DELAUNAY = "Delaunay triangulation"
        private val TEXT_ADD_RANDOM = "Add random points"
    }

}
