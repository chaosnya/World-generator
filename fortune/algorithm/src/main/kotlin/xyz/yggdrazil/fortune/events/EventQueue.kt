package xyz.yggdrazil.fortune.events

import java.util.*

open class EventQueue {

    private val points: SortedSet<EventPoint>
    private val listeners = ArrayList<EventQueueListener>()

    init {
        points = TreeSet(Comparator<xyz.yggdrazil.fortune.events.EventPoint> { e1, e2 ->
            if (e1.x != e2.x) {
                if (e1.x < e2.x) {
                    return@Comparator -1
                } else if (e1.x > e2.x) {
                    return@Comparator 1
                }
            }
            // e1.getX() == e2.getX()
            if (e1.y < e2.y) {
                return@Comparator -1
            } else if (e1.y > e2.y) {
                return@Comparator 1
            }
            // e1.getY() == e2.getY()
            val c1 = e1 is CirclePoint
            val c2 = e1 is CirclePoint
            if (c1 && !c2) {
                return@Comparator -1
            }
            if (!c1 && c2) {
                return@Comparator 1
            }
            // c1 == c2
            0
        })
    }

    @Synchronized fun size(): Int {
        return points.size
    }

    @Synchronized fun insert(eventPoint: EventPoint) {
        points.add(eventPoint)
        fireEventQueueChanged()
    }

    @Synchronized open fun remove(eventPoint: EventPoint): Boolean {
        val removed = points.remove(eventPoint)
        if (removed) {
            fireEventQueueChanged()
        }
        return removed
    }

    @Synchronized fun top(): EventPoint {
        return points.first()
    }

    @Synchronized open fun pop(): EventPoint {
        val point = points.first()
        points.remove(point)
        fireEventQueueChanged()
        return point
    }

    operator fun iterator(): Iterator<EventPoint> {
        return points.iterator()
    }

    val copy: EventQueue
        @Synchronized get() {
            val copy = EventQueue()
            for (point in points) {
                copy.insert(point)
            }
            return copy
        }

    @Synchronized operator fun get(index: Int): EventPoint? {
        // TODO: this is inefficient
        val iterator = iterator()
        var point: EventPoint? = null
        for (i in 0..index) {
            if (iterator.hasNext()) {
                point = iterator.next()
            } else {
                return null
            }
        }
        return point
    }

    fun addEventQueueListener(listener: EventQueueListener) {
        listeners.add(listener)
    }

    fun removeEventQueueListener(listener: EventQueueListener) {
        listeners.remove(listener)
    }

    private fun fireEventQueueChanged() {
        for (listener in listeners) {
            listener.update()
        }
    }

    open fun clear() {
        points.clear()
        fireEventQueueChanged()
    }

}
