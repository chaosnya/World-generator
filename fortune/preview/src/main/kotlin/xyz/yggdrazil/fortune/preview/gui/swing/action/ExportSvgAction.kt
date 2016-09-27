package xyz.yggdrazil.fortune.preview.gui.swing.action

import xyz.yggdrazil.fortune.preview.export.SvgExporter
import xyz.yggdrazil.fortune.preview.gui.swing.FileFilterSvg
import xyz.yggdrazil.fortune.preview.gui.swing.SwingFortune
import java.awt.event.ActionEvent
import java.io.IOException
import javax.swing.JFileChooser
import javax.xml.transform.TransformerException

class ExportSvgAction(swingFortune: SwingFortune) : SwingFortuneAction("Export SVG", "Export the current view to a SVG image", null, swingFortune) {

    override fun actionPerformed(e: ActionEvent) {
        val fc = JFileChooser()
        fc.currentDirectory = swingFortune.lastActiveDirectory
        fc.fileSelectionMode = JFileChooser.FILES_ONLY
        fc.fileFilter = FileFilterSvg()
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
            SvgExporter.exportSVG(file, algorithm, config, dimension.width,
                    dimension.height)
        } catch (ex: IOException) {
            println("unable to export image (IOException): " + ex.message)
        } catch (ex: TransformerException) {
            println("unable to export image (TransfomerException): " + ex.message)
        }

    }

}
