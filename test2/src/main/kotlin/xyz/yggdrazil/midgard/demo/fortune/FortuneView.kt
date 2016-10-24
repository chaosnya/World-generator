package xyz.yggdrazil.midgard.demo.fortune

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.control.SpinnerValueFactory
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.text.Font
import tornadofx.*
import xyz.yggdrazil.midgard.demo.component.spinner.LongSpinnerValueFactory
import xyz.yggdrazil.midgard.demo.selector.SelectDemoController

class FortuneView : View() {
    override val root: GridPane by fxml("/demo/views/FortuneView.fxml")
    val loginController: SelectDemoController by inject()
    val showDelaunay: CheckBox by fxid()
    val showVoronoi: CheckBox by fxid()
    val seed: Spinner<Long> by fxid()
    val sites: Spinner<Int> by fxid()
    val lloydRelaxations: Spinner<Int> by fxid()
    val model = FortuneModel()


    init {
        title = "Fortune Preview"
        sites.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(model.settings.sites, 1, Int.MAX_VALUE)
    }

    override fun onDock() {
        super.onDock()
        //lloydRelaxations.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(model.settings.lloydRelaxations, 0, Int.MAX_VALUE)
        //seed.valueFactory = LongSpinnerValueFactory(model.settings.seed, 1, Long.MAX_VALUE)
    }


}