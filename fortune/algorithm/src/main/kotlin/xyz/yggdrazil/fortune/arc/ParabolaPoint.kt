package xyz.yggdrazil.fortune.arc

import xyz.yggdrazil.fortune.events.CirclePoint
import xyz.yggdrazil.fortune.geometry.Point

open class ParabolaPoint(point: Point) : Point(point) {

    var a: Double = 0.toDouble()
        private set
    var b: Double = 0.toDouble()
        private set
    var c: Double = 0.toDouble()
        private set

    fun realX(): Double {
        return y
    }

    fun realY(d: Double): Double {
        return d - x
    }

    fun calculateCenter(next: Point, arcnode: ArcNode,
                        prev: Point): CirclePoint? {
        var circlepoint: CirclePoint? = null
        val p1 = Point(arcnode.x - next.x, arcnode.y - next.y)
        val p2 = Point(prev.x - arcnode.x, prev.y - arcnode.y)
        if (p2.y * p1.x > p2.x * p1.y) {
            val d = -p1.x / p1.y
            val d1 = next.y + p1.y / 2.0 - d * (next.x + p1.x / 2.0)
            val d2 = -p2.x / p2.y
            val d3 = arcnode.y + p2.y / 2.0 - d2 * (arcnode.x + p2.x / 2.0)
            val cx: Double
            val cy: Double
            if (p1.y == 0.0) {
                cx = next.x + p1.x / 2.0
                cy = d2 * cx + d3
            } else if (p2.y == 0.0) {
                cx = arcnode.x + p2.x / 2.0
                cy = d * cx + d1
            } else {
                cx = (d3 - d1) / (d - d2)
                cy = d * cx + d1
            }
            // cx, cy is the center of the circle through three points
            circlepoint = CirclePoint(cx, cy, arcnode)
        }
        return circlepoint
    }

    fun init(d: Double) {
        val d1 = realX()
        val d2 = realY(d)
        a = 1.0 / (2.0 * d2)
        b = -d1 / d2
        c = d1 * d1 / (2.0 * d2) + d2 / 2.0
    }

    fun f(y: Double): Double {
        return a * y * y + b * y + c
    }

    companion object {

        @Throws(MathException::class)
        fun solveQuadratic(da: Double, db: Double, dc: Double): DoubleArray {
            val ad = DoubleArray(2)
            val d3 = db * db - 4.0 * da * dc
            if (d3 < 0.0) {
                throw MathException()
            }
            if (da == 0.0) {
                if (db != 0.0) {
                    ad[0] = -dc / db
                } else {
                    throw MathException()
                }
            } else {
                val d4 = Math.sqrt(d3)
                val d5 = -db
                val d6 = 2.0 * da
                ad[0] = (d5 + d4) / d6
                ad[1] = (d5 - d4) / d6
            }
            return ad
        }
    }

}
