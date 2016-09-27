package xyz.yggdrazil.fortune.pointset

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.NumberFormat
import java.util.Locale

object PointSetReader {

    @Throws(IOException::class, ParseException::class)
    fun read(`in`: InputStream): PointSet {
        val pointSet = PointSet()

        val numberFormat = NumberFormat.getNumberInstance(Locale.US)

        val reader = BufferedReader(InputStreamReader(`in`))
        var line: String? = reader.readLine()
        while (line != null) {
            val parts = line.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size != 2) {
                throw ParseException("number of fields is not 2")
            }
            val sx = parts[0].trim { it <= ' ' }
            val sy = parts[1].trim { it <= ' ' }
            val x: Double
            val y: Double
            try {
                x = numberFormat.parse(sx).toDouble()
            } catch (e: java.text.ParseException) {
                throw ParseException("unable to parse x value: '" + sx
                        + "'")
            }

            try {
                y = numberFormat.parse(sy).toDouble()
            } catch (e: java.text.ParseException) {
                throw ParseException("unable to parse y value: '" + sy
                        + "'")
            }

            pointSet.add(Point(x, y))
            line = reader.readLine()
        }

        return pointSet
    }
}
