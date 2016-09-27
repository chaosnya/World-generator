package xyz.yggdrazil.fortune.preview.gui.swing.eventqueue

import xyz.yggdrazil.fortune.Algorithm
import java.awt.BorderLayout
import java.awt.Window
import javax.swing.JDialog
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane

class EventQueueDialog(parent: Window, algorithm: Algorithm) : JDialog(parent, "Event Queue") {

    init {

        val panel = JPanel(BorderLayout())
        contentPane = panel

        setSize(250, 500)

        val eventQueueModel = EventQueueModel(algorithm)

        val jsp = JScrollPane()
        val list = JList(eventQueueModel)
        jsp.setViewportView(list)

        panel.add(jsp, BorderLayout.CENTER)
    }

}
