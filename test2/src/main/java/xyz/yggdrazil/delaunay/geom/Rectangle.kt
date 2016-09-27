package xyz.yggdrazil.delaunay.geom

/**
 * Rectangle.java

 * @author Connor
 */
class Rectangle(val x: Double, val y: Double, val width: Double, val height: Double) {

    val right: Double
    val bottom: Double
    val left: Double
    val top: Double

    init {
        left = x
        top = y
        right = x + width
        bottom = y + height
    }

    fun liesOnAxes(p: Point): Boolean {
        return closeEnough(p.x, x, 1.0) || closeEnough(p.y, y, 1.0) || closeEnough(p.x, right, 1.0) || closeEnough(p.y, bottom, 1.0)
    }


}
