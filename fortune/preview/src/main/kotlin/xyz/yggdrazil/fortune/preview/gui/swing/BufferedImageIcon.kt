package xyz.yggdrazil.fortune.preview.gui.swing

import java.awt.Component
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.Icon

class BufferedImageIcon
/**
 * Create a new icon from this buffered image.

 * @param bi the image to wrap.
 */
(private val bi: BufferedImage) : Icon {

    override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
        g.drawImage(bi, x, y, null)
    }

    override fun getIconWidth(): Int {
        return bi.width
    }

    override fun getIconHeight(): Int {
        return bi.height
    }

}