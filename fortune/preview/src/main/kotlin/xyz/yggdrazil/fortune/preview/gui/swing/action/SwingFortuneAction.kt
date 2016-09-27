package xyz.yggdrazil.fortune.preview.gui.swing.action

import xyz.yggdrazil.fortune.preview.gui.swing.SwingFortune
import javax.swing.Icon

abstract class SwingFortuneAction(name: String, description: String, icon: Icon?,
                                  protected var swingFortune: SwingFortune) : BaseAction(name, description, icon) {

}
