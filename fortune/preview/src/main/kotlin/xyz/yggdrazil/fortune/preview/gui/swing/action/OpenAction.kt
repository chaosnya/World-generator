package xyz.yggdrazil.fortune.preview.gui.swing.action

import xyz.yggdrazil.fortune.geometry.Point
import xyz.yggdrazil.fortune.pointset.ParseException
import xyz.yggdrazil.fortune.pointset.PointSetReader
import xyz.yggdrazil.fortune.preview.gui.swing.FileFilterPointSet
import xyz.yggdrazil.fortune.preview.gui.swing.SwingFortune
import java.awt.event.ActionEvent
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import javax.swing.JFileChooser

class OpenAction(swingFortune: SwingFortune) : SwingFortuneAction("Open", "Open a point set", null, swingFortune) {

    override fun actionPerformed(e: ActionEvent) {
        val fc = JFileChooser()
        fc.currentDirectory = swingFortune.lastActiveDirectory
        fc.fileSelectionMode = JFileChooser.FILES_ONLY
        fc.fileFilter = FileFilterPointSet()
        val returnVal = fc.showOpenDialog(swingFortune)

        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return
        }

        val file = fc.selectedFile
        swingFortune.lastActiveDirectory = file.parentFile

        val fis: FileInputStream
        try {
            fis = FileInputStream(file)
            val bis = BufferedInputStream(fis)

            val pointSet = PointSetReader.read(bis)

            try {
                fis.close()
            } catch (ex: IOException) {
                // ignore
            }

            val sites = ArrayList<Point>()
            for (point in pointSet.getPoints()) {
                sites.add(Point(point.x, point.y))
            }
            swingFortune.algorithm.setSites(sites)
        } catch (ex: FileNotFoundException) {
            println("file not found: '$file'")
        } catch (ex: IOException) {
            println("IOException while reading point set: " + ex.message)
        } catch (ex: ParseException) {
            println("ParseException while reading point set: " + ex.message)
        }

    }

}
