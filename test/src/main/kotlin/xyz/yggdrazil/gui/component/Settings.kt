package xyz.yggdrazil.gui.component

/**
 * Created by Alexandre Mommers on 28/08/16.
 */

import xyz.yggdrazil.gui.model.Config
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.JToggleButton
import javax.swing.JToolBar

class Settings(private val canvas: Canvas, private val config: Config) : JToolBar(), ItemListener {

    init {

        isFloatable = false

        val `as` = arrayOf(TEXT_VORONOI, TEXT_DELAUNAY, TEXT_WATER)

        val buttons = arrayListOf<JToggleButton>()
        for (i in `as`.indices) {
            buttons.add(JToggleButton(`as`[i]))
            buttons[i].addItemListener(this)
            add(buttons[i])
        }

        buttons[0].isSelected = config.isDrawVoronoiLines
        buttons[1].isSelected = config.isDrawDelaunay
        buttons[2].isSelected = config.isDrawWater

    }

    override fun itemStateChanged(e: ItemEvent) {
        val button = e.item as JToggleButton
        val s = button.text
        val flag = button.isSelected
        if (s == TEXT_VORONOI) {
            config.isDrawVoronoiLines = flag
        } else if (s == TEXT_DELAUNAY) {
            config.isDrawDelaunay = flag
        } else if (s == TEXT_WATER) {
            config.isDrawWater = flag
        }

        canvas.repaint()
    }

    companion object {

        private val TEXT_VORONOI = "Voronoi diagram"
        private val TEXT_DELAUNAY = "Delaunay triangulation"
        private val TEXT_WATER = "Water"
    }

}
