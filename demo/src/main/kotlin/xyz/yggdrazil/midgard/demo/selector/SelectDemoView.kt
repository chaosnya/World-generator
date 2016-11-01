package xyz.yggdrazil.midgard.demo.selector

import javafx.scene.control.Button
import javafx.scene.layout.VBox
import tornadofx.FX
import tornadofx.View
import xyz.yggdrazil.midgard.demo.fortune.FortuneView

class SelectDemoView : View() {
    override val root: VBox by fxml("/demo/views/SelectorView.fxml")

    val fortuneDemo: Button by fxid()
    val mapDemo: Button by fxid()

    val fortuneView: FortuneView by inject()

    init {
        title = "Midgar map generator"

        fortuneDemo.setOnAction {
            if (FX.primaryStage.scene.root != fortuneView.root) {
                FX.primaryStage.scene.root = fortuneView.root
                FX.primaryStage.sizeToScene()
                FX.primaryStage.centerOnScreen()
            }
        }

        mapDemo.setOnAction {
            if (FX.primaryStage.scene.root != fortuneView.root) {
                FX.primaryStage.scene.root = fortuneView.root
                FX.primaryStage.sizeToScene()
                FX.primaryStage.centerOnScreen()
            }
        }
    }
}
