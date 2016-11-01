package xyz.yggdrazil.midgard.math.fortune

import xyz.yggdrazil.midgard.math.geometry.Point
import xyz.yggdrazil.midgard.math.geometry.Rectangle

/**
 * Created by Alexandre Mommers on 29/09/16.
 */

internal object BoundsCheck {

    val TOP = 1
    val BOTTOM = 2
    val LEFT = 4
    val RIGHT = 8

    /**
     * @param point
     * *
     * @param bounds
     * *
     * @return an int with the appropriate bits set if the Point lies on the
     * * corresponding bounds lines
     */
    fun check(point: Point, bounds: Rectangle): Int {
        var value = 0
        if (point.x == bounds.left) {
            value = value or LEFT
        }
        if (point.x == bounds.right) {
            value = value or RIGHT
        }
        if (point.y == bounds.top) {
            value = value or TOP
        }
        if (point.y == bounds.bottom) {
            value = value or BOTTOM
        }
        return value
    }

}
