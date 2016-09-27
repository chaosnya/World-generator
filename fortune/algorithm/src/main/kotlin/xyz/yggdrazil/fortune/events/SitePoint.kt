package xyz.yggdrazil.fortune.events

import xyz.yggdrazil.fortune.geometry.Point

class SitePoint : EventPoint {

    constructor(point: Point) : super(point) {
    }

    constructor(x: Double, y: Double) : super(x, y) {
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SitePoint) {
            return false
        }
        return other.x == x && other.y == y
    }
}
