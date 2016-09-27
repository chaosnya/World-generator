package xyz.yggdrazil.math.surface

import xyz.yggdrazil.math.Point
import xyz.yggdrazil.math.Vector
import xyz.yggdrazil.math.geometry.Polygon
import java.util.*


/**
 * Created by Alexandre Mommers on 23/09/16.
 */
class Plane(val x: Double, val y: Double, val width: Double, val height: Double) : Polygon() {

    init {
        add(Vector(x, y))
        add(Vector(x, height))
        add(Vector(width, height))
        add(Vector(width, y))
    }

    fun randomPointInside(): Point {
        val random = Random()

        val x = random.nextInt(width.toInt() - x.toInt()) + x
        val y = random.nextInt(height.toInt() - y.toInt()) + y

        return Point(x, y)
    }

}