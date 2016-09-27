package xyz.yggdrazil.fortune.geometry

open class Point {

    var x: Double = 0.toDouble()
    var y: Double = 0.toDouble()

    constructor(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    constructor(point: Point) {
        x = point.x
        y = point.y
    }

    fun distance(point: Point): Double {
        val dx = point.x - x
        val dy = point.y - y
        return Math.sqrt(dx * dx + dy * dy)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Point) {
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

}
