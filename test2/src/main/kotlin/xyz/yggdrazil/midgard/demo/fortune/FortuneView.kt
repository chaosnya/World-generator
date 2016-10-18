package xyz.yggdrazil.midgard.demo.fortune

import javafx.application.Platform
import javafx.scene.layout.BorderPane
import javafx.scene.text.Font
import tornadofx.*
import xyz.yggdrazil.midgard.demo.selector.SelectDemoController

class FortuneView : View() {
    override val root = BorderPane()
    val loginController: SelectDemoController by inject()

    init {
        title = "Secure FortuneView"

        with (root) {
            setPrefSize(800.0, 600.0)

            top {
                label(title) {
                    font = Font.font(22.0)
                }
            }

            center {

                label("If you can see this, you are successfully logged in!")

                hbox {

                    button("Logout") {
                        setOnAction {
                            loginController.logout()
                        }
                    }

                    button("Exit") {
                        setOnAction {
                            Platform.exit()
                        }
                    }
                }
            }
        }
    }
}