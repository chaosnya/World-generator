package xyz.yggdrazil

import xyz.yggdrazil.midgard.demo.DemoApp
import xyz.yggdrazil.midgard.math.fortune.Voronoi
import xyz.yggdrazil.midgard.test.examples.TestGraphImpl
import xyz.yggdrazil.midgard.test.voronoi.VoronoiGraph
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.WindowConstants

/**
 * Created by Alexandre Mommers on 28/09/2016.
 */
fun main(args: Array<String>) {


    return
    DemoApp.run()

    val bounds = 500
    val numSites = 30000
    val numLloydRelxations = 2
    val seed = System.nanoTime()
    println("seed: " + seed)

    val img = createVoronoiGraph(bounds, numSites, numLloydRelxations, seed).createMap()

    val file = File(String.format("output/seed-%s-sites-%d-lloyds-%d.png", seed, numSites, numLloydRelxations))
    file.mkdirs()
    ImageIO.write(img, "PNG", file)
    showImage(img)
}

fun showImage(image: BufferedImage) {
    val frame = object : JFrame() {
        override fun paint(g: Graphics?) {
            g?.drawImage(image, 25, 35, null)
        }
    }

    frame.title = "java fortune"
    frame.isVisible = true
    frame.setSize(image.width + 50, image.height + 50)
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
}

fun createVoronoiGraph(bounds: Int, numSites: Int, numLloydRelaxations: Int, seed: Long): VoronoiGraph {
    val r = Random(seed)

    //make the intial underlying voronoi structure
    val v = Voronoi.generate(numSites, bounds.toDouble(), bounds.toDouble(), r)

    //assemble the voronoi strucutre into a usable graph object representing a map
    val graph = TestGraphImpl(v, numLloydRelaxations, r)

    return graph
}