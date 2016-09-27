package xyz.yggdrazil.fortune.preview.gui.swing

import java.io.File
import javax.swing.filechooser.FileFilter

class FileFilterSvg : FileFilter() {

    override fun accept(path: File): Boolean {
        return path.isDirectory || path.name.endsWith(".svg")
    }

    override fun getDescription(): String {
        return "SVG images (*.svg)"
    }

}
