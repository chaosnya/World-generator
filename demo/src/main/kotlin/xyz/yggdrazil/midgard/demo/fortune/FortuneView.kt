package xyz.yggdrazil.midgard.demo.fortune

import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import tornadofx.View

class FortuneView : View() {
    override val root: GridPane by fxml("/demo/views/FortuneView.fxml")
    val showDelaunay: CheckBox by fxid()
    val showVoronoi: CheckBox by fxid()
    val seed: TextField by fxid()
    val updateRender: Button by fxid()
    val sites: Spinner<Int> by fxid()
    val lloydRelaxations: Spinner<Int> by fxid()
    val canvas: Canvas by fxid()
    val model = FortuneModel()
    val render = FortuneRender(canvas.graphicsContext2D)


    init {
        title = "Fortune Preview"

        //fortune model
        sites.valueFactory.value = model.settings.sites
        lloydRelaxations.valueFactory.value = model.settings.lloydRelaxations
        seed.text = "${model.settings.seed}"

        //render model
        showDelaunay.selectedProperty().value = render.settings.showDelaunay
        showVoronoi.selectedProperty().value = render.settings.showVoronoi

        updateRender.setOnAction {

            updateModel()
            updateRender()
        }
    }

    private fun updateModel() {
        //fortune model
        model.settings.seed = seed.text.toLong()
        model.settings.lloydRelaxations = lloydRelaxations.valueFactory.value
        model.settings.sites = sites.valueFactory.value

        //render model
        render.settings.showDelaunay = showDelaunay.selectedProperty().value
        render.settings.showVoronoi = showVoronoi.selectedProperty().value
    }

    private fun updateRender() {
        render.draw(
                model.voronoi,
                canvas.width,
                canvas.height
        )
    }
}