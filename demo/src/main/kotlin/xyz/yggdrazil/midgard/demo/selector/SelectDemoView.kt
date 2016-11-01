package xyz.yggdrazil.midgard.demo.selector

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.scene.control.CheckBox
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.util.Duration
import xyz.yggdrazil.midgard.demo.selector.Styles.Companion.loginScreen
import tornadofx.*
import xyz.yggdrazil.midgard.demo.selector.SelectDemoController

class SelectDemoView : View() {
    override val root = GridPane()
    val selectDemoController: SelectDemoController by inject()

    var username: TextField by singleAssign()
    var password: PasswordField by singleAssign()
    var remember: CheckBox by singleAssign()

    init {
        title = "Please log in"

        with (root) {
            addClass(loginScreen)

            row {
                button("Login") {
                    isDefaultButton = true

                    setOnAction {
                        selectDemoController.showFortuneView()
                    }
                }
            }

        }
    }

    fun clear() {
        username.clear()
        password.clear()
        remember.isSelected = false
    }


}
