package xyz.yggdrazil.fortune.preview.gui.swing.eventqueue

import xyz.yggdrazil.fortune.Algorithm
import xyz.yggdrazil.fortune.events.*
import javax.swing.AbstractListModel

class EventQueueModel(private val algorithm: Algorithm) : AbstractListModel<Any>(), EventQueueListener {
    private var copy: EventQueue? = null

    init {
        algorithm.eventQueue.addEventQueueListener(this)
    }

    override fun getSize(): Int {
        val copy = copy
        when (copy) {
            is EventQueue -> return copy.size()
            else -> return 0
        }
    }

    override fun getElementAt(index: Int): Any? {
        when (copy) {
            is EventQueue -> {
                val eventPoint = copy!![index]
                return Element(eventPoint!!)
            }
            else -> return null
        }
    }

    override fun update() {
        java.awt.EventQueue.invokeLater(object : Runnable {

            override fun run() {
                copy = algorithm.eventQueue.copy
                fireContentsChanged(this, 0, copy!!.size())
            }
        })
    }

    inner class Element(private val event: EventPoint) {

        override fun toString(): String {
            when (event) {
                is SitePoint -> return String.format("Site: %.1f, %.1f", event.x, event.y)
                is CirclePoint -> return String.format("Circle: %.1f, %.1f", event.x, event.y)
                else -> return ""
            }
        }

    }

    companion object {

        private val serialVersionUID = 1L
    }

}
