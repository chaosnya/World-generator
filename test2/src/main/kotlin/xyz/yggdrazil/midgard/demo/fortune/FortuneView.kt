package xyz.yggdrazil.midgard.demo.fortune

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.text.Font
import tornadofx.*
import xyz.yggdrazil.midgard.demo.component.spinner.LongSpinnerValueFactory
import xyz.yggdrazil.midgard.demo.selector.SelectDemoController

class FortuneView : View() {
    override val root: GridPane by fxml("/demo/views/FortuneView.fxml")
    val loginController: SelectDemoController by inject()
    val showDelaunay: CheckBox by fxid()
    val showVoronoi: CheckBox by fxid()
    val seed: TextField by fxid()
    val updateRender: Button by fxid()
    val sites: Spinner<Int> by fxid()
    val lloydRelaxations: Spinner<Int> by fxid()
    val canvas: Canvas by fxid()
    val model = FortuneModel()
    val render = FortuneRender()


    init {
        title = "Fortune Preview"
        sites.valueFactory.value = model.settings.sites
        lloydRelaxations.valueFactory.value = model.settings.lloydRelaxations
        seed.text = "${model.settings.seed}"

        updateRender.setOnAction {
            model.settings.seed = seed.text.toLong()
            model.settings.lloydRelaxations = lloydRelaxations.valueFactory.value
            model.settings.sites = sites.valueFactory.value

            render.draw(canvas.graphicsContext2D, model.voronoi)
        }

    }



}