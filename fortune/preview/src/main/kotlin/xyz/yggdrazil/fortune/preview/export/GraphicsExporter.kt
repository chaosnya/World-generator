package xyz.yggdrazil.fortune.preview.export

import xyz.yggdrazil.fortune.Algorithm
import xyz.yggdrazil.fortune.preview.gui.core.AlgorithmPainter
import xyz.yggdrazil.fortune.preview.gui.core.Config
import xyz.yggdrazil.fortune.preview.gui.swing.AwtPainter
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

object GraphicsExporter {

    @Throws(IOException::class)
    fun exportPNG(file: File, algorithm: Algorithm, config: Config,
                  width: Int, height: Int) {
        val image = BufferedImage(width, height,
                BufferedImage.TYPE_4BYTE_ABGR)

        val graphics = image.createGraphics()
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON)

        val painter = AwtPainter(graphics)

        val algorithmPainter = AlgorithmPainter(algorithm,
                config, painter)

        algorithmPainter.width = width
        algorithmPainter.height = height
        algorithmPainter.paint()

        ImageIO.write(image, "png", file)
    }

}
