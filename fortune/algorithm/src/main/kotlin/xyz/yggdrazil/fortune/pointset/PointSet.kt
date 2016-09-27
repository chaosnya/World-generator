package xyz.yggdrazil.fortune.pointset

import java.util.*

class PointSet {

    internal val points = ArrayList<Point>()

    fun add(point: Point) {
        points.add(point)
    }

    fun getPoints(): List<Point> {
        return Collections.unmodifiableList(points)
    }

}
