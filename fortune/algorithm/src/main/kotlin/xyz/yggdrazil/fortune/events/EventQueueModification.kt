package xyz.yggdrazil.fortune.events

class EventQueueModification(val x: Double, val type: EventQueueModification.Type, val eventPoint: EventPoint) {

    override fun toString(): String {
        return String.format("sweep: %f, type: %s, %s, point: %f,%f", x,
                type.toString(), eventPoint.javaClass, eventPoint.x,
                eventPoint.y)
    }

    enum class Type {
        ADD, REMOVE
    }
}
