package xyz.yggdrazil.delaunay.voronoi.nodename.as3delaunay

class LR(private val name: String) {

    override fun toString(): String {
        return name
    }

    companion object {

        val LEFT = LR("left")
        val RIGHT = LR("right")

        fun other(leftRight: LR): LR {
            return if (leftRight == LEFT) RIGHT else LEFT
        }
    }
}
