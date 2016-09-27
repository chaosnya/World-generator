package xyz.yggdrazil.fortune.preview.gui.swing

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import javax.swing.Icon

object ImageLoader {

    fun load(filename: String?): Icon? {
        if (filename == null) {
            return null
        }

        var bi: BufferedImage? = null
        try {
            val `is` = Thread.currentThread().contextClassLoader.getResourceAsStream(filename)
            bi = ImageIO.read(`is`)
            `is`.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (bi != null) {
            return BufferedImageIcon(bi)
        }

        // unable to load image
        return null
    }
}
