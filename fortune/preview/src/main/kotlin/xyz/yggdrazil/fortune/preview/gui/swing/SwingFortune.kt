package xyz.yggdrazil.fortune.preview.gui.swing

import xyz.yggdrazil.fortune.Algorithm
import xyz.yggdrazil.fortune.preview.gui.core.Config
import xyz.yggdrazil.fortune.preview.gui.swing.action.*
import xyz.yggdrazil.fortune.preview.gui.swing.eventqueue.EventQueueDialog
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.io.File
import javax.swing.*

class SwingFortune : JFrame("Fortune's sweep"), Runnable {
    /*
     * Various
	 */

    var algorithm = Algorithm()
        private set
    private var canvas: Canvas? = null
    private var controls: Controls? = null
    var config = Config()
        private set
    private var eventQueueDialog: EventQueueDialog? = null
    private var main: JPanel? = null
    private var menu: JMenuBar? = null
    private var settings: Settings? = null
    var isRunning = false
        private set
    var isForeward = true
    private var thread: Thread? = null
    private val wait = Any()
    var lastActiveDirectory: File? = null

    init {
        defaultCloseOperation = EXIT_ON_CLOSE

        init()
    }

    fun init() {
        /*
         * Menus
		 */

        menu = JMenuBar()

        val menuFile = JMenu("File")
        menu!!.add(menuFile)
        val open = JMenuItem(OpenAction(this))
        menuFile.add(open)
        val save = JMenuItem(SaveAction(this))
        menuFile.add(save)
        val exportBitmap = JMenuItem(ExportBitmapAction(this))
        menuFile.add(exportBitmap)
        val exportSvg = JMenuItem(ExportSvgAction(this))
        menuFile.add(exportSvg)
        val quit = JMenuItem(QuitAction())
        menuFile.add(quit)

        val menuHelp = JMenu("Help")
        menu!!.add(menuHelp)
        val about = JMenuItem("About")
        menuHelp.add(about)

        jMenuBar = menu

        /*
         * Components, layout
		 */

        main = JPanel()
        contentPane = main
        main!!.layout = BorderLayout()

        config.isDrawCircles = true
        config.isDrawBeach = true
        config.isDrawVoronoiLines = true
        config.isDrawDelaunay = false

        canvas = Canvas(algorithm, config)
        controls = Controls(this, algorithm)
        settings = Settings(canvas!!, config)

        val sweepControl = SweepControl(algorithm)
        sweepControl.setBorder(BorderFactory.createLineBorder(Color.BLACK))

        val south = Box(BoxLayout.Y_AXIS)
        south.add(sweepControl)
        south.add(controls)

        main!!.add(settings!!, BorderLayout.NORTH)
        main!!.add(canvas!!, BorderLayout.CENTER)
        main!!.add(south, BorderLayout.SOUTH)

        algorithm.addWatcher(canvas!!)
        algorithm.addWatcher(sweepControl)

        canvas!!.addComponentListener(object : ComponentAdapter() {

            override fun componentResized(e: ComponentEvent?) {
                algorithm.width = canvas!!.width
                algorithm.height = canvas!!.height
            }

        })

        setSize(800, 600)
        isVisible = true

        /*
         * EventQueue dialog
		 */

        eventQueueDialog = EventQueueDialog(this, algorithm)
        eventQueueDialog!!.isVisible = true
        eventQueueDialog!!.setLocation(x + width, location.getY().toInt())

        /*
         * Start thread
		 */

        thread = Thread(this)
        thread!!.start()
    }

    fun toggleRunning(): Boolean {
        if (isRunning) {
            isRunning = false
        } else {
            if (!algorithm.isFinshed) {
                isRunning = true
                synchronized(wait) {
                    (wait as Object).notify()
                }
            }
        }
        return isRunning
    }

    fun stopRunning() {
        if (!isRunning) {
            return
        }
        isRunning = false
    }

    /*
     * Open / Save dialogs related stuff
	 */

    override fun run() {
        while (true) {
            if (isRunning) {
                val eventsLeft: Boolean
                if (isForeward) {
                    eventsLeft = algorithm.nextPixel()
                } else {
                    eventsLeft = algorithm.previousPixel()
                }
                if (!eventsLeft) {
                    setPaused()
                }
                try {
                    Thread.sleep(15L)
                } catch (ex: InterruptedException) {
                    // ignore
                }

            } else {
                setPaused()
            }
        }
    }

    private fun setPaused() {
        isRunning = false
        controls!!.threadRunning(false)
        while (true) {
            try {
                synchronized(wait) {
                    (wait as Object).wait()
                }
                controls!!.threadRunning(true)
                break
            } catch (e: InterruptedException) {
                continue
            }

        }
    }

    val canvasSize: Dimension
        get() = canvas!!.size

}
