package xyz.yggdrazil.fortune

import xyz.yggdrazil.math.geometry.Point

import java.util.ArrayList

class HalfedgePriorityQueue // also known as heap
(private val ymin: Double, private val deltay: Double, sqrt_nsites: Int) {

    private var hash: ArrayList<Halfedge>? = null
    private var count: Int = 0
    private var minBucket: Int = 0
    private val hashsize: Int

    init {
        hashsize = 4 * sqrt_nsites

        var i: Int

        count = 0
        minBucket = 0
        hash = ArrayList<Halfedge>(hashsize)
        // dummy Halfedge at the top of each hash
        i = 0
        while (i < hashsize) {
            hash!!.add(Halfedge.createDummy())
            hash!![i].nextInPriorityQueue = null
            ++i
        }
    }

    fun dispose() {
        // get rid of dummies
        for (i in 0..hashsize - 1) {
            hash!![i].dispose()
        }
        hash!!.clear()
        hash = null
    }

    fun insert(halfEdge: Halfedge) {
        var previous: Halfedge
        var next: Halfedge?
        val insertionBucket = bucket(halfEdge)
        if (insertionBucket < minBucket) {
            minBucket = insertionBucket
        }
        previous = hash!![insertionBucket]
        next = previous.nextInPriorityQueue
        while (next is Halfedge && (halfEdge.ystar > next.ystar || halfEdge.ystar == next.ystar && halfEdge.vertex!!._x > next.vertex!!._x)) {
            previous = next
            next = previous.nextInPriorityQueue
        }
        halfEdge.nextInPriorityQueue = previous.nextInPriorityQueue
        previous.nextInPriorityQueue = halfEdge
        ++count
    }

    fun remove(halfEdge: Halfedge) {
        var previous: Halfedge
        val removalBucket = bucket(halfEdge)

        if (halfEdge.vertex != null) {
            previous = hash!![removalBucket]
            while (previous.nextInPriorityQueue != halfEdge) {
                previous = previous.nextInPriorityQueue!!
            }
            previous.nextInPriorityQueue = halfEdge.nextInPriorityQueue
            count--
            halfEdge.vertex = null
            halfEdge.nextInPriorityQueue = null
            halfEdge.dispose()
        }
    }

    private fun bucket(halfEdge: Halfedge): Int {
        var theBucket = ((halfEdge.ystar - ymin) / deltay * hashsize).toInt()
        if (theBucket < 0) {
            theBucket = 0
        }
        if (theBucket >= hashsize) {
            theBucket = hashsize - 1
        }
        return theBucket
    }

    private fun isEmpty(bucket: Int): Boolean {
        return hash!![bucket].nextInPriorityQueue == null
    }

    /**
     * move minBucket until it contains an actual Halfedge (not just the dummy
     * at the top);
     */
    private fun adjustMinBucket() {
        while (minBucket < hashsize - 1 && isEmpty(minBucket)) {
            ++minBucket
        }
    }

    fun empty(): Boolean {
        return count == 0
    }

    /**
     * @return coordinates of the Halfedge's vertex in V*, the transformed
     * * Voronoi diagram
     */
    fun min(): Point {
        adjustMinBucket()
        val answer = hash!![minBucket].nextInPriorityQueue
        return Point(answer!!.vertex!!._x, answer.ystar)
    }

    /**
     * remove and return the min Halfedge

     * @return
     */
    fun extractMin(): Halfedge {
        val answer: Halfedge

        // get the first real Halfedge in minBucket
        answer = hash!![minBucket].nextInPriorityQueue!!

        hash!![minBucket].nextInPriorityQueue = answer.nextInPriorityQueue
        count--
        answer.nextInPriorityQueue = null

        return answer
    }
}