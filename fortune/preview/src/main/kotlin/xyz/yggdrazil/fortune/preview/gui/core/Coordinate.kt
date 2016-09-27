package xyz.yggdrazil.fortune.preview.gui.core

import xyz.yggdrazil.fortune.geometry.Point

class Coordinate(var x: Double, var y: Double) {

    fun distance(point: Coordinate): Double {
        val dx = point.x - x
        val dy = point.y - y
        return Math.sqrt(dx * dx + dy * dy)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Coordinate) {
            return false
        }
        return other.x == x && other.y == y
    }

    override fun toString(): String {
        return "$x, $y"
    }

    override fun hashCode(): Int {
        val bitsX = java.lang.Double.doubleToLongBits(x)
        val bitsY = java.lang.Double.doubleToLongBits(x)
        val bits = bitsX + bitsY
        return (bits xor bits.ushr(32)).toInt()
    }

    constructor(point: Point) : this(point.x, point.y)

}
