package xyz.yggdrazil.midgard.math.geometry

/**
 * @author Connor
 */
class Point(var x: Double, var y: Double) {

    override fun toString(): String = "${x}, ${y}"

    fun length(): Double = Math.sqrt(x * x + y * y)

    companion object {

        fun distance(first: Point, second: Point): Double {
            return Math.sqrt((first.x - second.x) * (first.x - second.x) + (first.y - second.y) * (first.y - second.y))
        }
    }
}
