package xyz.yggdrazil.fortune.events

import xyz.yggdrazil.fortune.arc.ArcNode

class CirclePoint(x: Double, y: Double, val arc: ArcNode) : EventPoint(x, y) {

    val radius: Double

    init {
        radius = distance(arc)
        this.x += radius
    }

    override fun equals(other: Any?): Boolean {
        if (other !is CirclePoint) {
            return false
        }
        return other.x == x && other.y == y
    }
}
