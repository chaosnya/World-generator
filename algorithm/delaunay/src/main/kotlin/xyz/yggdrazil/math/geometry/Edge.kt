package xyz.yggdrazil.math.geometry

import xyz.yggdrazil.math.Vector

/**
 * Created by Alexandre Mommers on 19/09/16.
 */

class Edge(p1: Vector, p2: Vector) {

    var p1: Vector
        protected set
    var p2: Vector
        protected set

    init {
        this.p1 = p1
        this.p2 = p2
    }

    fun computeIntersection(s: Vector, p: Vector): Vector {
        val edgex = p1.x - p2.x
        val linex = s.x - p.x

        val m1 = (s.y - p.y) / (s.x - p.x)
        val m2 = (p1.y - p2.y) / (p1.x - p2.x)
        val b1 = s.y - m1 * s.x
        val b2 = p1.y - m2 * p1.x

        val x = if (edgex == 0.0)
            p1.x
        else if (linex == 0.0) p.x else (b2 - b1) / (m1 - m2)
        val y = if (linex == 0.0) m2 * x + b2 else m1 * x + b1

        return Vector(x, y)
    }

    fun computeIntersection(e: Edge): Vector {
        val edgex = p1.x - p2.x
        val linex = e.p1.x - e.p2.x

        val m1 = (e.p1.y - e.p2.y) / (e.p1.x - e.p2.x)
        val m2 = (p1.y - p2.y) / (p1.x - p2.x)
        val b1 = e.p1.y - m1 * e.p1.x
        val b2 = p1.y - m2 * p1.x

        val x = if (edgex == 0.0)
            p1.x
        else if (linex == 0.0) e.p2.x else (b2 - b1) / (m1 - m2)
        val y = if (linex == 0.0) m2 * x + b2 else m1 * x + b1

        return Vector(x, y)
    }

    fun generatePointOnEdge(t: Double): Vector {
        return p1 + (p2 * t)
    }

    fun distanceTo(p: Vector): Double {
        val u = ((p.x - p1.x) * (p2.x - p1.x) + (p.y - p1.y) * (p2.y - p1.y)) / (p2 - p1).length2()
        val ref = Vector(p1.x + u * (p2.x - p1.x),
                p1.y + u * (p2.y - p1.y), 0.0)
        return (ref - p).length()
    }

    companion object {

        fun isPointInsideEdge(e: Edge, ref: Vector, p: Vector): Boolean {
            val ve = e.p2 - e.p1
            val vr = ref - e.p1
            val vp = p - e.p1

            val A = ve.cross(vr)
            val B = ve.cross(vp)

            return A.z < 0 && B.z < 0 || A.z > 0 && B.z > 0
        }
    }
}

