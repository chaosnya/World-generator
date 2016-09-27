package xyz.yggdrazil.gui

import xyz.yggdrazil.gui.component.Canvas
import xyz.yggdrazil.gui.component.Controls
import xyz.yggdrazil.gui.component.Settings
import xyz.yggdrazil.gui.model.Config
import xyz.yggdrazil.gui.model.voronoi.VoronoiDiagram
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.JPanel


val appTitle = "Map generator"

class GUI : JFrame(appTitle) {

    private var canvas: Canvas
    private var controls: Controls
    private var config = Config()
        private set
    private var main = JPanel()
    private var settings: Settings
    private val model = VoronoiDiagram(config)

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        contentPane = main

        /*
         * Components, layout
		 */
        main.layout = BorderLayout()

        canvas = Canvas(config, model)
        controls = Controls(canvas, model)
        settings = Settings(canvas, config)

        main.add(settings, BorderLayout.NORTH)
        main.add(canvas, BorderLayout.CENTER)
        main.add(controls, BorderLayout.EAST)

        setSize(800, 600)
        isVisible = true

    }
}
