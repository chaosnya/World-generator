package xyz.yggdrazil.midgard.math.fortune

import xyz.yggdrazil.midgard.math.geometry.Point
import java.util.*

class Halfedge(edge: Edge?, lr: LR?) {
    var edgeListLeftNeighbor: Halfedge? = null
    var edgeListRightNeighbor: Halfedge? = null
    var nextInPriorityQueue: Halfedge? = null
    var edge: Edge? = null
    var leftRight: LR? = null
    var vertex: Vertex? = null
    // the vertex's y-coordinate in the transformed Voronoi space V*
    var ystar: Double = 0.toDouble()

    init {
        init(edge, lr)
    }

    private fun init(edge: Edge?, lr: LR?): Halfedge {
        this.edge = edge
        leftRight = lr
        nextInPriorityQueue = null
        vertex = null
        return this
    }

    override fun toString(): String {
        return "Halfedge (leftRight: $leftRight; vertex: $vertex)"
    }

    fun dispose() {
        if (edgeListLeftNeighbor != null || edgeListRightNeighbor != null) {
            // still in EdgeList
            return
        }
        if (nextInPriorityQueue != null) {
            // still in PriorityQueue
            return
        }
        edge = null
        leftRight = null
        vertex = null
        pool.push(this)
    }

    fun reallyDispose() {
        edgeListLeftNeighbor = null
        edgeListRightNeighbor = null
        nextInPriorityQueue = null
        edge = null
        leftRight = null
        vertex = null
        pool.push(this)
    }

    fun isLeftOf(p: Point): Boolean {
        val topSite: Site
        val rightOfSite: Boolean
        var above: Boolean
        var fast: Boolean
        val dxp: Double
        val dyp: Double
        val dxs: Double
        val t1: Double
        val t2: Double
        val t3: Double
        val yl: Double

        topSite = edge!!.rightSite
        rightOfSite = p.x > topSite._x
        if (rightOfSite && this.leftRight == LR.LEFT) {
            return true
        }
        if (!rightOfSite && this.leftRight == LR.RIGHT) {
            return false
        }

        if (edge!!.a == 1.0) {
            dyp = p.y - topSite._y
            dxp = p.x - topSite._x
            fast = false
            if (!rightOfSite && edge!!.b < 0.0 || rightOfSite && edge!!.b >= 0.0) {
                above = dyp >= edge!!.b * dxp
                fast = above
            } else {
                above = p.x + p.y * edge!!.b > edge!!.c
                if (edge!!.b < 0.0) {
                    above = !above
                }
                if (!above) {
                    fast = true
                }
            }
            if (!fast) {
                dxs = topSite._x - edge!!.leftSite._x
                above = edge!!.b * (dxp * dxp - dyp * dyp) < dxs * dyp * (1.0 + 2.0 * dxp / dxs + edge!!.b * edge!!.b)
                if (edge!!.b < 0.0) {
                    above = !above
                }
            }
        } else
        /* edge.b == 1.0 */ {
            yl = edge!!.c - edge!!.a * p.x
            t1 = p.y - yl
            t2 = p.x - topSite._x
            t3 = yl - topSite._y
            above = t1 * t1 > t2 * t2 + t3 * t3
        }
        return this.leftRight == LR.LEFT == above
    }

    companion object {

        private val pool = Stack<Halfedge>()

        fun create(edge: Edge?, lr: LR?): Halfedge {
            if (pool.size > 0) {
                return pool.pop().init(edge, lr)
            } else {
                return Halfedge(edge, lr)
            }
        }

        fun createDummy(): Halfedge {
            return create(null, null)
        }
    }
}
