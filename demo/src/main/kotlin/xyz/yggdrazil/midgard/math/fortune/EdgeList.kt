package xyz.yggdrazil.midgard.math.fortune

import xyz.yggdrazil.midgard.math.geometry.Point
import java.util.*

class EdgeList(private val xmin: Double, private val deltax: Double, sqrt_nsites: Int) {
    private val hashsize: Int
    private var hash: ArrayList<Halfedge?>
    var leftEnd: Halfedge? = null
    var rightEnd: Halfedge? = null

    fun dispose() {
        var halfEdge = leftEnd
        var prevHe: Halfedge?
        while (halfEdge != rightEnd) {
            prevHe = halfEdge
            halfEdge = halfEdge?.edgeListRightNeighbor
            prevHe?.dispose()
        }
        leftEnd = null
        rightEnd!!.dispose()
        rightEnd = null

        hash.clear()
    }

    init {
        hashsize = 2 * sqrt_nsites

        hash = ArrayList<Halfedge?>(hashsize)

        // two dummy Halfedges:
        leftEnd = Halfedge.createDummy()
        rightEnd = Halfedge.createDummy()
        leftEnd!!.edgeListLeftNeighbor = null
        leftEnd!!.edgeListRightNeighbor = rightEnd
        rightEnd!!.edgeListLeftNeighbor = leftEnd
        rightEnd!!.edgeListRightNeighbor = null

        for (i in 0..hashsize - 1) {
            hash.add(null)
        }

        hash[0] = leftEnd
        hash[hashsize - 1] = rightEnd
    }

    /**
     * Insert newHalfedge to the right of lb

     * @param lb
     * *
     * @param newHalfedge
     */
    fun insert(lb: Halfedge, newHalfedge: Halfedge) {
        newHalfedge.edgeListLeftNeighbor = lb
        newHalfedge.edgeListRightNeighbor = lb.edgeListRightNeighbor
        lb.edgeListRightNeighbor?.edgeListLeftNeighbor = newHalfedge
        lb.edgeListRightNeighbor = newHalfedge
    }

    /**
     * This function only removes the Halfedge from the left-right list. We
     * cannot dispose it yet because we are still using it.

     * @param halfEdge
     */
    fun remove(halfEdge: Halfedge) {
        halfEdge.edgeListLeftNeighbor?.edgeListRightNeighbor = halfEdge.edgeListRightNeighbor
        halfEdge.edgeListRightNeighbor?.edgeListLeftNeighbor = halfEdge.edgeListLeftNeighbor
        halfEdge.edge = Edge.DELETED
        halfEdge.edgeListLeftNeighbor = null
        halfEdge.edgeListRightNeighbor = null
    }

    /**
     * Find the rightmost Halfedge that is still left of p

     * @param p
     * *
     * @return
     */
    fun edgeListLeftNeighbor(p: Point): Halfedge {
        var i: Int
        var bucket: Int
        var halfEdge: Halfedge?

        /* Use hash table to get close to desired halfedge */
        bucket = ((p.x - xmin) / deltax * hashsize).toInt()
        if (bucket < 0) {
            bucket = 0
        }
        if (bucket >= hashsize) {
            bucket = hashsize - 1
        }
        halfEdge = getHash(bucket)
        if (halfEdge == null) {
            i = 1
            while (true) {
                halfEdge = getHash(bucket - i)
                if (halfEdge != null) {
                    break
                }
                halfEdge = getHash(bucket + i)
                if (halfEdge != null) {
                    break
                }
                ++i
            }
        }
        /* Now search linear list of halfedges for the correct one */
        if (halfEdge == leftEnd || halfEdge != rightEnd && halfEdge!!.isLeftOf(p)) {
            do {
                halfEdge = halfEdge!!.edgeListRightNeighbor
            } while (halfEdge != rightEnd && halfEdge!!.isLeftOf(p))
            halfEdge = halfEdge!!.edgeListLeftNeighbor
        } else {
            do {
                halfEdge = halfEdge!!.edgeListLeftNeighbor
            } while (halfEdge != leftEnd && !halfEdge!!.isLeftOf(p))
        }

        /* Update hash table and reference counts */
        if (bucket > 0 && bucket < hashsize - 1) {
            hash.set(bucket, halfEdge)
        }
        return halfEdge!!
    }

    /* Get entry from hash table, pruning any deleted nodes */
    private fun getHash(b: Int): Halfedge? {
        val halfEdge: Halfedge?

        if (b < 0 || b >= hashsize) {
            return null
        }
        halfEdge = hash[b]
        if (halfEdge != null && halfEdge.edge == Edge.DELETED) {
            /* Hash table points to deleted halfedge.  Patch as necessary. */
            hash.set(b, null)
            // still can't dispose halfEdge yet!
            return null
        } else {
            return halfEdge
        }
    }
}