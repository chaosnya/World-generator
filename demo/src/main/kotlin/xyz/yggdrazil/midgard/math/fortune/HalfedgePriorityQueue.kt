package xyz.yggdrazil.midgard.math.fortune

import xyz.yggdrazil.midgard.math.geometry.Point
import java.util.*

// also known as heap
class HalfedgePriorityQueue(private val ymin: Double, private val deltay: Double, sqrt_nsites: Int) {

    private var hash: ArrayList<Halfedge>
    private var count = 0
    private var minBucket = 0
    private val hashSize = 4 * sqrt_nsites

    init {
        hash = ArrayList<Halfedge>(hashSize)
        // dummy Halfedge at the top of each hash
        for (i in 0..hashSize - 1) {
            hash.add(Halfedge.createDummy())
            hash[i].nextInPriorityQueue = null
        }
    }

    fun dispose() {
        // get rid of dummies
        for (i in 0..hashSize - 1) {
            hash[i].dispose()
        }
        hash.clear()
    }

    fun insert(halfEdge: Halfedge) {
        var previous: Halfedge
        var next: Halfedge?
        val insertionBucket = bucket(halfEdge)
        if (insertionBucket < minBucket) {
            minBucket = insertionBucket
        }
        previous = hash[insertionBucket]
        next = previous.nextInPriorityQueue
        while (next is Halfedge && (halfEdge.ystar > next.ystar || halfEdge.ystar == next.ystar && halfEdge.vertex!!.x > next.vertex!!.x)) {
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
            previous = hash[removalBucket]
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
        var theBucket = ((halfEdge.ystar - ymin) / deltay * hashSize).toInt()
        if (theBucket < 0) {
            theBucket = 0
        }
        if (theBucket >= hashSize) {
            theBucket = hashSize - 1
        }
        return theBucket
    }

    private fun isEmpty(bucket: Int): Boolean {
        return hash[bucket].nextInPriorityQueue == null
    }

    /**
     * move minBucket until it contains an actual Halfedge (not just the dummy
     * at the top);
     */
    private fun adjustMinBucket() {
        while (minBucket < hashSize - 1 && isEmpty(minBucket)) {
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
        val answer = hash[minBucket].nextInPriorityQueue
        return Point(answer!!.vertex!!.x, answer.ystar)
    }

    /**
     * remove and return the min Halfedge

     * @return
     */
    fun extractMin(): Halfedge {
        val answer: Halfedge

        // get the first real Halfedge in minBucket
        answer = hash[minBucket].nextInPriorityQueue!!

        hash[minBucket].nextInPriorityQueue = answer.nextInPriorityQueue
        count--
        answer.nextInPriorityQueue = null

        return answer
    }
}