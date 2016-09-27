package xyz.yggdrazil.fortune.arc

import xyz.yggdrazil.fortune.events.HistoryEventQueue
import xyz.yggdrazil.fortune.geometry.Point

class ArcTree {

    var arcs: ArcNode? = null
        private set

    fun insert(point: Point, sweepX: Double, eventQueue: HistoryEventQueue) {
        if (arcs == null) {
            arcs = ArcNode(point)
            return
        }
        try {
            val parabolaPoint = ParabolaPoint(point)
            parabolaPoint.init(sweepX)
            arcs!!.init(sweepX)
            arcs!!.insert(parabolaPoint, sweepX, eventQueue)
            return
        } catch (e: MathException) {
            println("*** error: No parabola intersection during ArcTree.insert()")
        }

    }

    fun remove(point: Point) {
        val size = size()
        if (size == 0) {
            return
        }
        if (size == 1) {
            arcs = null
            return
        }
        var iter = arcs
        while (iter != null) {
            if (iter.x == point.x && iter.y == point.y) {
                val prev = iter.previous
                val next = iter.next
                if (prev == next) {
                    prev?.next = next?.next
                    prev?.next?.let { next ->
                        next.previous = prev
                    }
                    prev?.startOfTrace = next?.startOfTrace
                }
                break
            }
            iter = iter.next
        }
    }

    fun size(): Int {
        if (arcs == null) {
            return 0
        }
        var a = arcs
        var size = 0
        while (a != null) {
            size += 1
            a = a.next
        }
        return size
    }

    fun clear() {
        arcs = null
    }

}
