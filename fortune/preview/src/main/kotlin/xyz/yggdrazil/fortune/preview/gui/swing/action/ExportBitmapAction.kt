package xyz.yggdrazil.fortune.preview.gui.swing.action

import xyz.yggdrazil.fortune.preview.export.GraphicsExporter
import xyz.yggdrazil.fortune.preview.gui.swing.FileFilterBitmap
import xyz.yggdrazil.fortune.preview.gui.swing.SwingFortune
import java.awt.event.ActionEvent
import java.io.IOException
import javax.swing.JFileChooser

class ExportBitmapAction(swingFortune: SwingFortune) : SwingFortuneAction("Export Bitmap", "Export the current view to a bitmap", null, swingFortune) {

    override fun actionPerformed(e: ActionEvent) {
        val fc = JFileChooser()
        fc.currentDirectory = swingFortune.lastActiveDirectory
        fc.fileSelectionMode = JFileChooser.FILES_ONLY
        fc.fileFilter = FileFilterBitmap()
        val returnVal = fc.showSaveDialog(swingFortune)

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return
        }

        val file = fc.selectedFile
        swingFortune.lastActiveDirectory = file.parentFile

        val algorithm = swingFortune.algorithm
        val config = swingFortune.config
        val dimension = swingFortune.canvasSize

        try {
            GraphicsExporter.exportPNG(file, algorithm, config,
                    dimension.width, dimension.height)
        } catch (ex: IOException) {
            println("unable to export image: " + ex.message)
        }

    }

    companion object {

        private val serialVersionUID = 1L
    }

}
