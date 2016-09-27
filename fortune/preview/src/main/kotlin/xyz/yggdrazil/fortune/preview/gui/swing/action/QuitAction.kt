package xyz.yggdrazil.fortune.preview.gui.swing.action

import java.awt.event.ActionEvent

class QuitAction : BaseAction("Quit", "Exit the application", null) {

    override fun actionPerformed(e: ActionEvent) {
        System.exit(0)
    }

}
