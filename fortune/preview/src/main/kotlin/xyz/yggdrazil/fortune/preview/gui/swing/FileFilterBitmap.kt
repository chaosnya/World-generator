package xyz.yggdrazil.fortune.preview.gui.swing

import java.io.File
import javax.swing.filechooser.FileFilter

class FileFilterBitmap : FileFilter() {

    override fun accept(path: File): Boolean {
        return path.isDirectory || path.name.endsWith(".png")
    }

    override fun getDescription(): String {
        return "PNG images (*.png)"
    }

}
