package xyz.yggdrazil.math.geometry

import xyz.yggdrazil.math.Vector
import java.util.*

/**
 * Created by Alexandre Mommers on 19/09/16.
 */
open class Polygon : ArrayList<Vector> {


    constructor() : super() {
    }

    constructor(vararg points: Vector) : super() {
        this.addAll(points)
    }

    fun clippingAlg(subjectP: ArrayList<Vector>, clippingP: List<Edge>): ArrayList<Vector> {
        val edges = clippingP
        var input = subjectP
        val out = ArrayList<Vector>()

        //begin looping through edges and points
        var casenum = 0
        for (i in 0..edges.size - 1) {
            val e = edges.get(i)
            val r = edges.get((i + 2) % edges.size).p1
            var s = input.last()
            for (j in 0..input.size - 1) {
                val p = input[j]

                //first see if the point is inside the edge
                if (Edge.isPointInsideEdge(e, r, p)) {
                    //then if the specific pair of points is inside
                    if (Edge.isPointInsideEdge(e, r, s)) {
                        casenum = 1
                        //pair goes outside, so one point still inside
                    } else {
                        casenum = 4
                    }

                    //no point inside
                } else //does the specific pair go inside
                    if (Edge.isPointInsideEdge(e, r, s)) {
                        casenum = 2
                        //no points in pair are inside
                    } else {
                        casenum = 3
                    }

                when (casenum) {

                //pair is inside, add point
                    1 -> out.add(p)

                //pair goes inside, add intersection only
                    2 -> {
                        val inter0 = e.computeIntersection(s, p)
                        out.add(inter0)
                    }

                //pair outside, add nothing
                    3 -> {
                    }

                //pair goes outside, add point and intersection
                    4 -> {
                        val inter1 = e.computeIntersection(s, p)
                        out.add(inter1)
                        out.add(p)
                    }
                }
                s = p
            }
            input = out.clone() as ArrayList<Vector>
            out.clear()
        }
        return input
    }

    //conducts the Sutherland-Hodgman clipping algorithm
    //update polygon with giving boundary
    fun clip(edges: List<Edge>) {
        var newPolygon = Polygon()
        var polygon = this

        //begin looping through edges and points
        var casenum = 0
        for (i in 0..edges.size - 1) {
            val e = edges.get(i)
            val r = edges.get((i + 2) % edges.size).p1
            var s = this[this.size - 1]
            for (j in 0..this.size - 1) {
                val p = this[j]

                //first see if the point is inside the edge
                if (Edge.isPointInsideEdge(e, r, p)) {
                    //then if the specific pair of points is inside
                    if (Edge.isPointInsideEdge(e, r, s)) {
                        casenum = 1
                        //pair goes outside, so one point still inside
                    } else {
                        casenum = 4
                    }

                    //no point inside
                } else //does the specific pair go inside
                    if (Edge.isPointInsideEdge(e, r, s)) {
                        casenum = 2
                        //no points in pair are inside
                    } else {
                        casenum = 3
                    }

                when (casenum) {

                //pair is inside, add point
                    1 -> newPolygon.add(p)

                //pair goes inside, add intersection only
                    2 -> {
                        val inter0 = e.computeIntersection(s, p)
                        newPolygon.add(inter0)
                    }

                //pair outside, add nothing
                    3 -> {
                    }

                //pair goes outside, add point and intersection
                    4 -> {
                        val inter1 = e.computeIntersection(s, p)
                        newPolygon.add(inter1)
                        newPolygon.add(p)
                    }
                }
                s = p
            }
            polygon = newPolygon
            newPolygon = Polygon()
        }

        /* update current polygon */
        clear()
        addAll(polygon)
    }

    fun centroid(): Vector {

        var centroidX = 0.0
        var centroidY = 0.0

        for (point in this) {
            centroidX += point.x
            centroidY += point.y
        }
        return Vector(centroidX / this.count(), centroidY / this.count())
    }

    class InconsistentPolygonException : RuntimeException("a polygon must contains at least 3 edges") {

    }

    fun edges(): List<Edge> {
        val edges = ArrayList<Edge>()

        if (size < 3) {
            throw InconsistentPolygonException()
        }

        forEachIndexed { index, point ->
            if (last() == point) {
                edges.add(Edge(point, first()))
            } else {
                edges.add(Edge(point, this[index + 1]))
            }
        }

        return edges
    }
}