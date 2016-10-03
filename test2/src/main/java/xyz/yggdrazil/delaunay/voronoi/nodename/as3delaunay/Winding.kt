package xyz.yggdrazil.delaunay.voronoi.nodename.as3delaunay

class Winding private constructor(private val name: String) {

    override fun toString(): String {
        return name
    }

    companion object {

        val CLOCKWISE = Winding("clockwise")
        val COUNTERCLOCKWISE = Winding("counterclockwise")
        val NONE = Winding("none")
    }
}