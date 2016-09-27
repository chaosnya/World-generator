package xyz.yggdrazil.delaunay.voronoi.nodename.as3delaunay

import xyz.yggdrazil.delaunay.geom.Point

class Circle(centerX: Double, centerY: Double, var radius: Double) {

    var center: Point

    init {
        this.center = Point(centerX, centerY)
    }

    override fun toString(): String {
        return "Circle (center: $center; radius: $radius)"
    }
}