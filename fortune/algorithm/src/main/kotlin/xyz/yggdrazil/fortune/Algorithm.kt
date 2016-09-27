package xyz.yggdrazil.fortune

import xyz.yggdrazil.fortune.arc.ArcTree
import xyz.yggdrazil.fortune.events.*
import xyz.yggdrazil.fortune.geometry.Edge
import xyz.yggdrazil.fortune.geometry.Point
import java.util.*

class Algorithm {

    /*
     * Data structures to maintain voronoi diagram and xyz.yggdrazil.delaunay triangulation.
     */
    /*
     * Various getters
	 */

    var voronoi = Voronoi()
        private set
    var delaunay: Delaunay? = null
        private set

    /*
     * Current position of the sweepline.
     */
    var sweepX: Double = 0.toDouble()
        private set

    /*
     * Dimension of the area of interest.
     */
    var height: Int = 0
    /*
     * Dimension getters / setters
	 */

    var width: Int = 0

    /*
     * A list with the initial sites
     */
    private var sites: MutableList<Point> = ArrayList<Point>()

    /*
     * Event queue with site and circle events plus a special pointer to the
     * currently active event.
     */
    val eventQueue = HistoryEventQueue(this)
    var currentEvent: EventPoint? = null
        private set

    /*
     * This maintains a list of events that have been executed during the
     * algorithm so that exactly these events may be reverted when playing the
     * algorithm backwards.
     */
    private var executedEvents: Stack<EventPoint>? = null

    /*
     * The beachline data structure.
     */
    val arcs = ArcTree()

    /*
     * Watchers that need to be notified once the algorithm moved to a new
     * state.
     */
    private val watchers = ArrayList<AlgorithmWatcher>()

    init {
        init()
    }

    /*
     * Public API
	 */

    fun addSite(point: Point) {
        val inserted = eventQueue.insertEvent(SitePoint(point))
        if (inserted) {
            sites.add(point)
            voronoi.addSite(point)
            voronoi.checkDegenerate()
        }
    }

    fun addWatcher(watcher: AlgorithmWatcher) {
        watchers.add(watcher)
    }

    fun removeWatcher(watcher: AlgorithmWatcher) {
        watchers.remove(watcher)
    }

    fun getSites(): List<Point> {
        return Collections.unmodifiableList(sites)
    }

    fun setSites(sites: MutableList<Point>) {
        this.sites = sites
        voronoi = Voronoi()
        for (point in sites) {
            voronoi.addSite(point)
        }
        restart()
    }

    /*
     * Internal methods
	 */

    private fun notifyWatchers() {
        for (watcher in watchers) {
            watcher.update()
        }
    }

    @Synchronized private fun init() {
        sweepX = 0.0
        arcs.clear()
        eventQueue.clear()
        executedEvents = Stack<EventPoint>()
        currentEvent = null
        voronoi.clear()
        delaunay = Delaunay()
        for (point in sites) {
            val inserted = eventQueue.insertEvent(SitePoint(point))
            if (inserted) {
                voronoi.addSite(point)
            }
        }
    }

    /*
     * Sweepline control
	 */

    @Synchronized fun nextPixel(): Boolean {
        return moveForward(1.0)
    }

    @Synchronized fun previousPixel(): Boolean {
        return moveBackward(1.0)
    }

    @Synchronized fun moveForward(amount: Double): Boolean {
        sweepX += amount
        currentEvent = null

        val xPosOld = sweepX
        while (eventQueue.size() != 0 && xPosOld >= eventQueue.top().x) {
            val eventPoint = eventQueue.pop()
            sweepX = eventPoint.x
            process(eventPoint)
            currentEvent = eventPoint
        }
        sweepX = xPosOld

        if (currentEvent != null && sweepX > currentEvent!!.x) {
            currentEvent = null
        }

        notifyWatchers()
        return !isFinshed
    }

    @Synchronized fun moveBackward(amount: Double): Boolean {
        if (sweepX <= 0) {
            return false
        }
        val xPosBefore = sweepX
        sweepX -= amount
        currentEvent = null

        restoreEventQueue(xPosBefore)

        /*
         * Go through executed events and revert everything within the interval
		 */
        while (executedEvents!!.size > 0) {
            val lastEvent = executedEvents!!.peek()
            if (!(lastEvent.x >= sweepX && lastEvent.x <= xPosBefore)) {
                break
            }
            executedEvents!!.pop()
            if (lastEvent is SitePoint) {
                revert(lastEvent)
            } else if (lastEvent is CirclePoint) {
                revert(lastEvent)
            }
        }

        notifyWatchers()
        return sweepX > 0
    }

    private fun restoreEventQueue(xPosBefore: Double) {
        /*
         * Restore event queue
		 */
        while (eventQueue.hasModification()) {
            val mod = eventQueue.latestModification
            val event = eventQueue.latestModification?.eventPoint
            if (event is SitePoint && mod is EventQueueModification) {
                if (!(mod.x >= sweepX && mod.x <= xPosBefore)) {
                    break
                }
            } else if (event is CirclePoint && mod is EventQueueModification) {
                if (mod.type == EventQueueModification.Type.REMOVE) {
                    if (!(mod.x >= sweepX && mod.x <= xPosBefore)) {
                        break
                    }
                } else if (mod.type == EventQueueModification.Type.ADD) {
                    if (!(mod.x >= sweepX && mod.x <= xPosBefore)) {
                        break
                    }
                }
            }
            eventQueue.revertModification()
        }
    }

    @Synchronized fun setSweep(x: Double) {
        if (sweepX < x) {
            moveForward(x - sweepX)
        } else if (sweepX > x) {
            moveBackward(sweepX - x)
        }

        notifyWatchers()
    }

    val isFinshed: Boolean
        @Synchronized get() = !(eventQueue.size() != 0 || sweepX < PLAY_N_PIXELS_BEYOND_SCREEN + width)

    @Synchronized fun nextEvent() {
        if (eventQueue.size() > 0) {
            val eventPoint = eventQueue.pop()
            sweepX = eventPoint.x
            process(eventPoint)
            currentEvent = eventPoint
        } else if (sweepX < width) {
            sweepX = width.toDouble()
            currentEvent = null
        }
        notifyWatchers()
    }

    fun previousEvent() {
        if (executedEvents!!.isEmpty()) {
            // If we are before the first event but after 0, just go to 0.
            if (sweepX > 0) {
                sweepX = 0.0
                notifyWatchers()
            }
            return
        }

        val xPosBefore = sweepX

        var point: EventPoint? = executedEvents!!.pop()
        if (sweepX > point!!.x) {
            // If we are beyond some event
            sweepX = point.x
            restoreEventQueue(xPosBefore)
        } else if (sweepX == point.x) {
            // If we are exactly at some event
            restoreEventQueue(xPosBefore)

            if (point is SitePoint) {
                revert(point)
            } else if (point is CirclePoint) {
                revert(point)
            }
            if (executedEvents!!.isEmpty()) {
                point = null
            } else {
                point = executedEvents!!.pop()
                sweepX = point.x
                restoreEventQueue(xPosBefore)
            }
        }
        if (point == null) {
            // If no executed events are left, we go to 0
            sweepX = 0.0
            currentEvent = null
        } else {
            // Revert the event that we just arrived at
            if (point is SitePoint) {
                revert(point)
            } else if (point is CirclePoint) {
                revert(point)
            }
            // Replay the event that we just arrived at
            eventQueue.pop()
            process(point)
            currentEvent = point
        }
        notifyWatchers()
    }

    @Synchronized fun clear() {
        sites = ArrayList<Point>()
        voronoi = Voronoi()
        restart()
    }

    @Synchronized fun restart() {
        init()
        notifyWatchers()
    }

    /*
     * Internal event processing
	 */

    private fun process(eventPoint: EventPoint) {
        // Remember that this event has been executed
        executedEvents!!.push(eventPoint)

        // Actually execute the event depending on its type
        if (eventPoint is SitePoint) {
            process(eventPoint)
        } else if (eventPoint is CirclePoint) {
            process(eventPoint)
        }
    }

    // Site events

    private fun process(sitePoint: SitePoint) {
        arcs.insert(sitePoint, sweepX, eventQueue)
    }

    private fun revert(sitePoint: SitePoint) {
        arcs.remove(sitePoint)
    }

    // Circle events

    private fun process(circlePoint: CirclePoint) {
        // arc is the disappearing arc
        val arc = circlePoint.arc

        // prev and next are the new neighbors on the beachline
        val prev = arc.previous
        val next = arc.next

        // point is the position of the new voronoi vertex
        val point = Point(circlePoint.x - circlePoint.radius,
                circlePoint.y)

        // Add two new voronoi edges
        prev?.completeTrace(this, point)
        arc.completeTrace(this, point)

        // Add a new trace
        prev?.startOfTrace = point

        // Change arc pointers
        prev?.next = next
        next?.previous = prev

        // Dismiss now invalid circle events
        prev?.circlePoint?.let { circlePoint ->
            eventQueue.remove(circlePoint)
            prev.circlePoint = null
        }

        next?.circlePoint?.let { circlePoint ->
            eventQueue.remove(circlePoint)
            next.circlePoint = null
        }

        // Check for new circle events
        prev?.checkCircle(eventQueue)
        next?.checkCircle(eventQueue)
    }

    private fun revert(circlePoint: CirclePoint) {

        // Reinsert arc between previous and next
        val arc = circlePoint.arc
        arc.next?.previous = arc
        arc.previous?.next = arc

        // Restore trace starting at removed voronoi vertex
        val point = Point(circlePoint.x - circlePoint.radius,
                circlePoint.y)
        arc.uncompleteTrace()
        arc.previous?.uncompleteTrace()

        // Remove vertex/edges from voronoi diagram
        voronoi.removeLinesFromVertex(point)

        // Remove edge from xyz.yggdrazil.delaunay triangulation. Remove each each twice with
        // inverted coordinates to make sure equals() works with one of them.
        delaunay?.let { delaunay ->
            arc.previous?.let { previous ->
                delaunay.remove(Edge(arc, previous))
                delaunay.remove(Edge(previous, arc))
            }

            arc.next?.let { next ->
                delaunay.remove(Edge(arc, next))
                delaunay.remove(Edge(next, arc))
            }
        }
    }

    companion object {
        private val PLAY_N_PIXELS_BEYOND_SCREEN = 1000
    }

}
