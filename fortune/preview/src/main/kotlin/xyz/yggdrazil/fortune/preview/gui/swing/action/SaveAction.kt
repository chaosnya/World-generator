package xyz.yggdrazil.fortune.preview.gui.swing.action

import xyz.yggdrazil.fortune.pointset.PointSet
import xyz.yggdrazil.fortune.pointset.PointSetWriter
import xyz.yggdrazil.fortune.preview.gui.swing.FileFilterPointSet
import xyz.yggdrazil.fortune.preview.gui.swing.SwingFortune
import java.awt.event.ActionEvent
import java.io.BufferedOutputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.swing.JFileChooser

class SaveAction(swingFortune: SwingFortune) : SwingFortuneAction("Save", "Save the current point set", null, swingFortune) {

    override fun actionPerformed(e: ActionEvent) {
        val fc = JFileChooser()
        fc.currentDirectory = swingFortune.lastActiveDirectory
        fc.fileSelectionMode = JFileChooser.FILES_ONLY
        fc.fileFilter = FileFilterPointSet()
        val returnVal = fc.showSaveDialog(swingFortune)

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return
        }

        val file = fc.selectedFile
        swingFortune.lastActiveDirectory = file.parentFile

        val sites = swingFortune.algorithm.getSites()
        val pointSet = PointSet()
        for (site in sites) {
            val point = xyz.yggdrazil.fortune.pointset.Point(site.x,
                    site.y)
            pointSet.add(point)
        }
        try {
            val fos = FileOutputStream(file)
            val bos = BufferedOutputStream(fos)

            PointSetWriter.write(pointSet, bos)

            bos.close()
        } catch (ex: FileNotFoundException) {
            println("file not found: '$file'")
        } catch (ex: IOException) {
            println("unable to close output file")
        }

    }

}
