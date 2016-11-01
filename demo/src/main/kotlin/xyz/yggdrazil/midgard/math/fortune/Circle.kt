package xyz.yggdrazil.midgard.math.fortune

import xyz.yggdrazil.midgard.math.geometry.Point

class Circle(centerX: Double, centerY: Double, var radius: Double) {

    var center: Point

    init {
        this.center = Point(centerX, centerY)
    }

    override fun toString(): String {
        return "Circle (center: $center; radius: $radius)"
    }
}