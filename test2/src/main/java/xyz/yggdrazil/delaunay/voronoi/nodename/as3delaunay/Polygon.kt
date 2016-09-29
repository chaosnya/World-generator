package xyz.yggdrazil.delaunay.voronoi.nodename.as3delaunay

import xyz.yggdrazil.delaunay.geom.Point
import java.util.*

class Polygon(private val vertices: ArrayList<Point>) {
    
    fun winding(): Winding {
        val signedDoubleArea = signedDoubleArea()
        if (signedDoubleArea < 0) {
            return Winding.CLOCKWISE
        }
        if (signedDoubleArea > 0) {
            return Winding.COUNTERCLOCKWISE
        }
        return Winding.NONE
    }

    private fun signedDoubleArea(): Double {
        var index: Int
        var nextIndex: Int
        val n = vertices.size
        var point: Point
        var next: Point
        var signedDoubleArea = 0.0
        index = 0
        while (index < n) {
            nextIndex = (index + 1) % n
            point = vertices[index]
            next = vertices[nextIndex]
            signedDoubleArea += point.x * next.y - next.x * point.y
            ++index
        }
        return signedDoubleArea
    }
}