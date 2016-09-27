package xyz.yggdrazil.fortune.events

import xyz.yggdrazil.fortune.geometry.Point

open class EventPoint : Point {

    constructor(point: Point) : super(point) {
    }

    constructor(x: Double, y: Double) : super(x, y) {
    }

}
