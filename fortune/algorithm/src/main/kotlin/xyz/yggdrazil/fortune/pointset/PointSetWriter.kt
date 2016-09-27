package xyz.yggdrazil.fortune.pointset

import java.io.IOException
import java.io.OutputStream
import java.io.PrintWriter
import java.text.NumberFormat
import java.util.Locale

object PointSetWriter {

    @Throws(IOException::class)
    fun write(pointSet: PointSet, out: OutputStream) {
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)

        val printer = PrintWriter(out)
        for (point in pointSet.points) {
            printer.println(numberFormat.format(point.x) + ", "
                    + numberFormat.format(point.y))
        }

        printer.close()
    }
}
