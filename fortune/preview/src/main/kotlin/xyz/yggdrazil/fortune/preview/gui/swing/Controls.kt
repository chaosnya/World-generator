package xyz.yggdrazil.fortune.preview.gui.swing

import xyz.yggdrazil.fortune.Algorithm
import java.awt.Panel
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import javax.swing.Icon
import javax.swing.JButton

class Controls(private val fortune: SwingFortune, private val algorithm: Algorithm) : Panel(), ActionListener {
    private val buttons = ArrayList<JButton>()
    private val icons = HashMap<String, Icon>()

    init {

        for (key in paths.keys) {
            ImageLoader.load(paths[key])?.let { icon ->
                icons.put(key, icon)
            }
        }

        val keys = arrayOf(KEY_PLAY, KEY_PLAY_REVERSE, KEY_PREVIOUS_EVENT, KEY_NEXT_EVENT, KEY_PREV_PIXEL, KEY_NEXT_PIXEL, KEY_RESTART, KEY_CLEAR)
        for (i in keys.indices) {
            buttons.add(JButton(icons[keys[i]]))
            buttons[i].toolTipText = texts[keys[i]]
            buttons[i].addActionListener(this)
            add(buttons[i])
        }

        threadRunning(false)
    }

    override fun actionPerformed(e: ActionEvent) {
        var i = 0
        if (e.source === buttons[i++]) {
            if (fortune.isForeward) {
                val running = fortune.toggleRunning()
                threadRunning(running)
            } else {
                fortune.isForeward = true
                if (!fortune.isRunning) {
                    fortune.toggleRunning()
                }
            }
            threadRunning(fortune.isRunning)
        } else if (e.source === buttons[i++]) {
            if (!fortune.isForeward) {
                val running = fortune.toggleRunning()
                threadRunning(running)
            } else {
                fortune.isForeward = false
                if (!fortune.isRunning) {
                    fortune.toggleRunning()
                }
            }
            threadRunning(fortune.isRunning)
        } else if (e.source === buttons[i++]) {
            algorithm.previousEvent()
        } else if (e.source === buttons[i++]) {
            algorithm.nextEvent()
        } else if (e.source === buttons[i++]) {
            algorithm.previousPixel()
        } else if (e.source === buttons[i++]) {
            algorithm.nextPixel()
        } else if (e.source === buttons[i++]) {
            algorithm.restart()
        } else if (e.source === buttons[i]) {
            fortune.stopRunning()
            threadRunning(false)
            algorithm.clear()
        }
    }

    fun threadRunning(running: Boolean) {
        if (running) {
            if (fortune.isForeward) {
                set(buttons[0], KEY_PAUSE)
                set(buttons[1], KEY_PLAY_REVERSE)
            } else {
                set(buttons[0], KEY_PLAY)
                set(buttons[1], KEY_PAUSE)
            }
            buttons[4].isEnabled = false
            buttons[5].isEnabled = false
        } else {
            set(buttons[0], KEY_PLAY)
            set(buttons[1], KEY_PLAY_REVERSE)
            buttons[4].isEnabled = true
            buttons[5].isEnabled = true
        }
        buttons[0].invalidate()
        invalidate()
        validate()
    }

    private operator fun set(button: JButton, key: String) {
        button.toolTipText = texts[key]
        button.icon = icons[key]
    }

    companion object {

        private val serialVersionUID = -8452143409724541737L
        private val KEY_PLAY = "play"
        private val KEY_PLAY_REVERSE = "play-reverse"
        private val KEY_PAUSE = "pause"
        private val KEY_PREVIOUS_EVENT = "previous-event"
        private val KEY_NEXT_EVENT = "next-event"
        private val KEY_PREV_PIXEL = "previous-pixel"
        private val KEY_NEXT_PIXEL = "next-pixel"
        private val KEY_CLEAR = "clear"
        private val KEY_RESTART = "restart"
        private val texts = HashMap<String, String>()
        private val paths = HashMap<String, String>()

        init {
            texts.put(KEY_PLAY, "Play")
            texts.put(KEY_PLAY_REVERSE, "Play Reverse")
            texts.put(KEY_PAUSE, "Pause")
            texts.put(KEY_PREVIOUS_EVENT, "Previous event")
            texts.put(KEY_NEXT_EVENT, "Next event")
            texts.put(KEY_PREV_PIXEL, "Previous pixel")
            texts.put(KEY_NEXT_PIXEL, "Next pixel")
            texts.put(KEY_CLEAR, "Clear")
            texts.put(KEY_RESTART, "Restart")
        }

        init {
            paths.put(KEY_PLAY, "res/media-playback-start.png")
            paths.put(KEY_PLAY_REVERSE, "res/media-playback-start-rtl.png")
            paths.put(KEY_PAUSE, "res/media-playback-pause.png")
            paths.put(KEY_PREVIOUS_EVENT, "res/media-skip-backward.png")
            paths.put(KEY_NEXT_EVENT, "res/media-skip-forward.png")
            paths.put(KEY_PREV_PIXEL, "res/media-seek-backward.png")
            paths.put(KEY_NEXT_PIXEL, "res/media-seek-backward-rtl.png")
            paths.put(KEY_CLEAR, "res/media-eject.png")
            paths.put(KEY_RESTART, "res/media-playback-stop.png")
        }
    }
}
