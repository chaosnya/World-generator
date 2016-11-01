package xyz.yggdrazil.midgard.math.fortune

import java.util.*

class EdgeReorderer(origEdges: ArrayList<Edge>, criterion: Class<*>) {

    var edges: ArrayList<Edge>? = null
        private set
    var edgeOrientations: ArrayList<LR>? = null
        private set

    init {
        if (criterion != Vertex::class.java && criterion != Site::class.java) {
            throw Error("Edges: criterion must be Vertex or Site")
        }
        edges = ArrayList<Edge>()
        edgeOrientations = ArrayList<LR>()
        if (origEdges.size > 0) {
            edges = reorderEdges(origEdges, criterion)
        }
    }

    fun dispose() {
        edges = null
        edgeOrientations = null
    }

    private fun reorderEdges(origEdges: ArrayList<Edge>, criterion: Class<*>): ArrayList<Edge> {
        var i: Int
        val n = origEdges.size
        var edge: Edge
        // we're going to reorder the edges in order of traversal
        val done = ArrayList<Boolean>(n)
        var nDone = 0
        for (k in 0..n - 1) {
            done.add(false)
        }
        val newEdges = ArrayList<Edge>()

        i = 0
        edge = origEdges[i]
        newEdges.add(edge)
        edgeOrientations!!.add(LR.LEFT)
        var firstPoint = if (criterion == Vertex::class.java) edge.leftVertex else edge.leftSite
        var lastPoint = if (criterion == Vertex::class.java) edge.rightVertex else edge.rightSite

        if (firstPoint === Vertex.VERTEX_AT_INFINITY || lastPoint === Vertex.VERTEX_AT_INFINITY) {
            return ArrayList()
        }

        done[i] = true
        ++nDone

        while (nDone < n) {
            i = 1
            while (i < n) {
                if (done.get(i)) {
                    ++i
                    continue
                }
                edge = origEdges[i]
                val leftPoint = if (criterion == Vertex::class.java) edge.leftVertex else edge.leftSite
                val rightPoint = if (criterion == Vertex::class.java) edge.rightVertex else edge.rightSite
                if (leftPoint === Vertex.VERTEX_AT_INFINITY || rightPoint === Vertex.VERTEX_AT_INFINITY) {
                    return ArrayList()
                }
                if (leftPoint === lastPoint) {
                    lastPoint = rightPoint
                    edgeOrientations!!.add(LR.LEFT)
                    newEdges.add(edge)
                    done.set(i, true)
                } else if (rightPoint === firstPoint) {
                    firstPoint = leftPoint
                    edgeOrientations!!.add(0, LR.LEFT)
                    newEdges.add(0, edge)
                    done[i] = true
                } else if (leftPoint === firstPoint) {
                    firstPoint = rightPoint
                    edgeOrientations!!.add(0, LR.RIGHT)
                    newEdges.add(0, edge)

                    done[i] = true
                } else if (rightPoint === lastPoint) {
                    lastPoint = leftPoint
                    edgeOrientations!!.add(LR.RIGHT)
                    newEdges.add(edge)
                    done[i] = true
                }
                if (done[i]) {
                    ++nDone
                }
                ++i
            }
        }

        return newEdges
    }
}