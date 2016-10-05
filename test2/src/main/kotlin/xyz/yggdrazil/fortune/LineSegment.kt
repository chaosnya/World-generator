package xyz.yggdrazil.fortune

import xyz.yggdrazil.delaunay.geom.Point

class LineSegment(var p0: Point?, var p1: Point?) {
    companion object {

        fun compareLengths_MAX(segment0: LineSegment, segment1: LineSegment): Double {
            val length0 = Point.distance(segment0.p0!!, segment0.p1!!)
            val length1 = Point.distance(segment1.p0!!, segment1.p1!!)
            if (length0 < length1) {
                return 1.0
            }
            if (length0 > length1) {
                return -1.0
            }
            return 0.0
        }


    }
}