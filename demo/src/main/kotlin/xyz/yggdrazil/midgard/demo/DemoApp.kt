package xyz.yggdrazil.midgard.demo

import javafx.stage.Stage
import xyz.yggdrazil.midgard.demo.selector.SelectDemoController
import xyz.yggdrazil.midgard.demo.selector.SelectDemoView
import xyz.yggdrazil.midgard.demo.selector.Styles
import tornadofx.App
import tornadofx.importStylesheet

class DemoApp : App() {
    override val primaryView = SelectDemoView::class
    val loginController: SelectDemoController by inject()

    override fun start(stage: Stage) {
        importStylesheet(Styles::class)
        super.start(stage)
        loginController.init()
    }

    companion object {

        fun run() {
            launch(DemoApp::class.java, null)
        }
    }
}

