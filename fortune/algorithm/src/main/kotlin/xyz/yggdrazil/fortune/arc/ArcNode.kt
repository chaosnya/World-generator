package xyz.yggdrazil.fortune.arc

import xyz.yggdrazil.fortune.Algorithm
import xyz.yggdrazil.fortune.events.CirclePoint
import xyz.yggdrazil.fortune.events.HistoryEventQueue
import xyz.yggdrazil.fortune.geometry.Edge
import xyz.yggdrazil.fortune.geometry.Point
import java.util.*

class ArcNode(point: Point) : ParabolaPoint(point) {
    var next: ArcNode? = null
    var previous: ArcNode? = null
    var circlePoint: CirclePoint? = null
    var startOfTrace: Point? = null
    private val startOfTraceBackup = Stack<Point>()

    fun checkCircle(eventQueue: HistoryEventQueue) {
        if (previous != null && next != null) {
            circlePoint = calculateCenter(next!!, this, previous!!)
            if (circlePoint != null) {
                eventQueue.insertEvent(circlePoint!!)
            }
        }
    }

    fun removeCircle(eventQueue: HistoryEventQueue) {
        circlePoint?.let { circlePoint ->
            eventQueue.remove(circlePoint)
            this.circlePoint = null
        }
    }

    fun completeTrace(algorithm: Algorithm, point: Point) {
        startOfTrace?.let { startOfTrace ->
            algorithm.voronoi.addLine(Edge(startOfTrace, point))
            algorithm.delaunay!!.add(Edge(this, next!!))
            startOfTraceBackup.push(startOfTrace)
            this.startOfTrace = null
        }
    }

    fun uncompleteTrace() {
        startOfTrace = startOfTraceBackup.pop()
    }

    @Throws(MathException::class)
    fun insert(parabolaPoint: ParabolaPoint, sweepX: Double,
               eventQueue: HistoryEventQueue) {
        var split = true
        if (next != null) {
            next!!.init(sweepX)
            if (sweepX > next!!.x && sweepX > x) {
                val xs = ParabolaPoint.Companion.solveQuadratic(a - next!!.a, b - next!!.b, c - next!!.c)
                if (xs[0] <= parabolaPoint.realX() && xs[0] != xs[1]) {
                    split = false
                }
            } else {
                split = false
            }
        }

        if (split) {
            removeCircle(eventQueue)

            /*
             * insert new arc and update pointers
			 */

            val arcnode = ArcNode(parabolaPoint)
            arcnode.next = ArcNode(this)
            arcnode.previous = this
            arcnode.next!!.next = next
            arcnode.next!!.previous = arcnode

            if (next != null) {
                next!!.previous = arcnode.next
            }

            next = arcnode

            /*
             * circle events
			 */

            checkCircle(eventQueue)
            next!!.next!!.checkCircle(eventQueue)

            /*
             * traces
			 */

            next!!.next!!.startOfTrace = startOfTrace
            startOfTrace = Point(sweepX - f(parabolaPoint.y),
                    parabolaPoint.y)
            next!!.startOfTrace = Point(sweepX - f(parabolaPoint.y),
                    parabolaPoint.y)
        } else {
            next!!.insert(parabolaPoint, sweepX, eventQueue)
        }
    }

}
