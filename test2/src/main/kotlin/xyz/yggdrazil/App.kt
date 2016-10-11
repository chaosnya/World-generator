package xyz.yggdrazil

import xyz.yggdrazil.delaunay.examples.TestGraphImpl
import xyz.yggdrazil.delaunay.voronoi.VoronoiGraph
import xyz.yggdrazil.fortune.Voronoi
import java.awt.Color
import java.awt.Graphics
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.WindowConstants

/**
 * Created by Alexandre Mommers on 28/09/2016.
 */
fun main(args: Array<String>) {
    val bounds = 1000
    val numSites = 30000
    val numLloydRelxations = 2
    val seed = System.nanoTime()
    println("seed: " + seed)

    val img = createVoronoiGraph(bounds, numSites, numLloydRelxations, seed).createMap()

    val file = File(String.format("output/seed-%s-sites-%d-lloyds-%d.png", seed, numSites, numLloydRelxations))
    file.mkdirs()
    ImageIO.write(img, "PNG", file)

    val frame = object : JFrame() {
        override fun paint(g: Graphics?) {
            g!!.drawImage(img, 25, 35, null)
        }
    }

    frame.title = "java fortune"
    frame.isVisible = true
    frame.setSize(img.width + 50, img.height + 50)
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
}


fun createVoronoiGraph(bounds: Int, numSites: Int, numLloydRelaxations: Int, seed: Long): VoronoiGraph {
    val r = Random(seed)

    //make the intial underlying voronoi structure
    val v = Voronoi(numSites, bounds.toDouble(), bounds.toDouble(), r, ArrayList<Color>())

    //assemble the voronoi strucutre into a usable graph object representing a map
    val graph = TestGraphImpl(v, numLloydRelaxations, r)

    return graph
}