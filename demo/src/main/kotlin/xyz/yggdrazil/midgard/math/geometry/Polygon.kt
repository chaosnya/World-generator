package xyz.yggdrazil.midgard.math.geometry

import java.util.*

/**
 * Created by Alexandre Mommers on 04/11/16.
 */
open class Polygon : ArrayList<Point>() {

    fun centroid(): Point {
        var x = .0
        var y = .0

        this.forEach { point ->
            x += point.x
            y += point.y
        }

        x /= size.toDouble()
        y /= size.toDouble()

        return Point(x, y)
    }
}