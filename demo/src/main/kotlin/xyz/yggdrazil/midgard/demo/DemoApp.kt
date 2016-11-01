package xyz.yggdrazil.midgard.demo

import tornadofx.App
import xyz.yggdrazil.midgard.demo.selector.SelectDemoView

class DemoApp : App() {
    override val primaryView = SelectDemoView::class
    
    companion object {

        fun run() {
            launch(DemoApp::class.java, null)
        }
    }
}

