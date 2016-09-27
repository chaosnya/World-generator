package xyz.yggdrazil.fortune

import xyz.yggdrazil.fortune.geometry.Edge
import xyz.yggdrazil.fortune.geometry.Point
import java.util.*

class Voronoi {
    val sites = ArrayList<Point>()
    val edges = ArrayList<Edge>()
    val pointToEdges = HashMap<Point, MutableList<Edge>>()

    init {
        checkDegenerate()
    }

    fun checkDegenerate() {
        if (sites.size > 1) {
            var min = sites[0]
            var next = min
            for (i in 1..sites.size - 1) {
                val element = sites[i]
                if (element.x <= min.x) {
                    next = min
                    min = element
                } else if (element.x <= min.x) {
                    next = element
                }
            }

            if (min.x == next.x && min !== next) {
                min.x = min.x - 1
                println("Moved point: " + next.x + " -> "
                        + min.x)
            }
        }
    }

    fun clear() {
        edges.clear()
    }

    fun addSite(site: Point) {
        sites.add(site)
    }

    fun addLine(edge: Edge) {
        edges.add(edge)
        var start: MutableList<Edge>? = pointToEdges[edge.start]
        if (start == null) {
            start = ArrayList<Edge>()
            pointToEdges.put(edge.start, start)
        }
        start.add(edge)
        var end: MutableList<Edge>? = pointToEdges[edge.end]
        if (end == null) {
            end = ArrayList<Edge>()
            pointToEdges.put(edge.end, end)
        }
        end.add(edge)
    }

    fun removeLinesFromVertex(point: Point) {
        val edges = pointToEdges[point]
        for (edge in edges!!) {
            this.edges.remove(edge)
        }
    }
}
