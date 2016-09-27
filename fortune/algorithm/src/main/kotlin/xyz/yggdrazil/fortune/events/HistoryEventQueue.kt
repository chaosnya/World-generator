package xyz.yggdrazil.fortune.events

import xyz.yggdrazil.fortune.Algorithm
import java.util.*

class HistoryEventQueue(private val algorithm: Algorithm) : EventQueue() {

    private val modifications = ArrayList<EventQueueModification>()

    @Synchronized fun insertEvent(eventPoint: EventPoint): Boolean {
        if (eventPoint is CirclePoint) {
            val modification = EventQueueModification(
                    algorithm.sweepX, EventQueueModification.Type.ADD, eventPoint)
            // Circle events will just be appended
            modifications.add(modification)
            insert(eventPoint)
            return true
        } else if (eventPoint is SitePoint) {
            val modification = EventQueueModification(
                    0.0, EventQueueModification.Type.ADD, eventPoint)
            // Site events need to be inserted at the correct position
            val pos = findLastSiteInsertion()
            modifications.add(pos + 1, modification)
            insert(eventPoint)
            return true
        }
        return false
    }

    /**
     * Find the position of the last insertion of a SitePoint. Since all
     * SitePoint insertion are stored as a sequence at the beginning of the
     * modifications list, the returned value is the index of the last SitePoint
     * insertion of that sequence.

     * @return the index of the last SitePoint insertion in the sequence of
     * * SitePoint insertions or -1 if there has not been any SitePoint
     * * insertion yet.
     */
    private fun findLastSiteInsertion(): Int {
        var pos = -1
        for (i in modifications.indices) {
            val mod = modifications[i]
            if (mod.type == EventQueueModification.Type.ADD && mod.eventPoint is SitePoint) {
                pos = i
            } else {
                break
            }
        }
        return pos
    }

    @Synchronized override fun remove(eventPoint: EventPoint): Boolean {
        val remove = super.remove(eventPoint)
        if (remove) {
            modifications.add(EventQueueModification(algorithm.sweepX,
                    EventQueueModification.Type.REMOVE, eventPoint))
        }
        return remove
    }

    @Synchronized override fun pop(): EventPoint {
        val eventPoint = top()
        modifications.add(EventQueueModification(eventPoint.x,
                EventQueueModification.Type.REMOVE, eventPoint))
        return super.pop()
    }

    @Synchronized fun hasModification(): Boolean {
        return modifications.size > 0
    }

    val latestModification: EventQueueModification?
        @Synchronized get() {
            if (modifications.size == 0) {
                return null
            }
            return modifications[modifications.size - 1]
        }

    @Synchronized fun revertModification(): EventQueueModification? {
        if (modifications.size == 0) {
            return null
        }
        val modification = modifications.removeAt(modifications.size - 1)
        // Reverse EventQueue modification
        if (modification.type == EventQueueModification.Type.ADD) {
            // Remove if the event was added
            if (modification.eventPoint is CirclePoint) {
                super.remove(modification.eventPoint)
            }
        } else if (modification.type == EventQueueModification.Type.REMOVE) {
            // Insert if the event was removed
            insert(modification.eventPoint)
            // Revert pointers of arcs to their circle events.
            if (modification.eventPoint is CirclePoint) {
                val circlePoint = modification.eventPoint
                circlePoint.arc.circlePoint = circlePoint
            }
        }

        return modification
    }

    override fun clear() {
        super.clear()
        modifications.clear()
    }

}
