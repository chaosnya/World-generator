package xyz.yggdrazil.fortune.geometry

class Edge(val start: Point, val end: Point) {

    override fun equals(other: Any?): Boolean {
        if (other !is Edge) {
            return false
        }
        return other.start == start && other.end == end
    }

}
