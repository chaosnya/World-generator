package xyz.yggdrazil.midgard.math.fortune

import xyz.yggdrazil.midgard.math.geometry.Point

class LineSegment(val p0: Point, val p1: Point) {
    companion object {

        fun compareLengths_MAX(segment0: LineSegment, segment1: LineSegment): Double {
            val length0 = Point.distance(segment0.p0, segment0.p0)
            val length1 = Point.distance(segment1.p0, segment1.p1)
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