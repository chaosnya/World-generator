package xyz.yggdrazil.fortune

import xyz.yggdrazil.math.geometry.Point
import java.util.*

class Vertex(x: Double, y: Double) : ICoord {

    override var coord: Point? = null

    var vertexIndex: Int = 0
        private set

    init {
        init(x, y)
    }

    private fun init(x: Double, y: Double): Vertex {
        coord = Point(x, y)
        return this
    }

    fun dispose() {
        coord = null
        pool.push(this)
    }

    fun setIndex() {
        vertexIndex = nvertices++
    }

    override fun toString(): String {
        return "Vertex ($vertexIndex)"
    }

    val _x: Double
        get() = coord!!.x

    val _y: Double
        get() = coord!!.y

    companion object {

        val VERTEX_AT_INFINITY = Vertex(java.lang.Double.NaN, java.lang.Double.NaN)
        private val pool = Stack<Vertex>()
        private var nvertices = 0

        private fun create(x: Double, y: Double): Vertex {

            if (java.lang.Double.isNaN(x) || java.lang.Double.isNaN(y)) {
                return VERTEX_AT_INFINITY
            }
            if (pool.size > 0) {

                return pool.pop().init(x, y)
            } else {
                return Vertex(x, y)
            }
        }

        /**
         * This is the only way to make a Vertex

         * @param halfedge0
         * *
         * @param halfedge1
         * *
         * @return
         */
        fun intersect(halfedge0: Halfedge, halfedge1: Halfedge): Vertex? {
            val edge0: Edge?
            val edge1: Edge?
            val edge: Edge
            val halfedge: Halfedge
            val determinant: Double
            val intersectionX: Double
            val intersectionY: Double
            val rightOfSite: Boolean

            edge0 = halfedge0.edge
            edge1 = halfedge1.edge
            if (edge0 == null || edge1 == null) {
                return null
            }
            if (edge0.rightSite == edge1.rightSite) {
                return null
            }

            determinant = edge0.a * edge1.b - edge0.b * edge1.a
            if (-1.0e-10 < determinant && determinant < 1.0e-10) {
                // the edges are parallel
                return null
            }

            intersectionX = (edge0.c * edge1.b - edge1.c * edge0.b) / determinant
            intersectionY = (edge1.c * edge0.a - edge0.c * edge1.a) / determinant

            if (compareByYThenX(edge0.rightSite, edge1.rightSite) < 0) {
                halfedge = halfedge0
                edge = edge0
            } else {
                halfedge = halfedge1
                edge = edge1
            }
            rightOfSite = intersectionX >= edge.rightSite._x
            if (rightOfSite && halfedge.leftRight == LR.LEFT || !rightOfSite && halfedge.leftRight == LR.RIGHT) {
                return null
            }

            return Companion.create(intersectionX, intersectionY)
        }
    }
}
