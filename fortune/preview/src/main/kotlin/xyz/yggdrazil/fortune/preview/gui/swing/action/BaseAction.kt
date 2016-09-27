package xyz.yggdrazil.fortune.preview.gui.swing.action

import xyz.yggdrazil.fortune.preview.gui.swing.ImageLoader
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.Icon

abstract class BaseAction(private val name: String, private val description: String, private var icon: Icon?) : AbstractAction() {

    override fun getValue(key: String): Any? {
        if (key == Action.SMALL_ICON) {
            return icon
        } else if (key == Action.NAME) {
            return name
        } else if (key == Action.SHORT_DESCRIPTION) {
            return description
        }
        return null
    }

    protected fun setIconFromResource(filename: String) {
        icon = ImageLoader.load(filename)
    }

    companion object {

        private val serialVersionUID = 1L
    }
}
