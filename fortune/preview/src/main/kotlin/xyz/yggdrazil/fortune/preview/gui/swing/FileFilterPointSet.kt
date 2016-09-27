package xyz.yggdrazil.fortune.preview.gui.swing

import java.io.File
import javax.swing.filechooser.FileFilter

class FileFilterPointSet : FileFilter() {

    override fun accept(path: File): Boolean {
        return path.isDirectory || path.name.endsWith(".points")
    }

    override fun getDescription(): String {
        return "point sets (*.points)"
    }

}
