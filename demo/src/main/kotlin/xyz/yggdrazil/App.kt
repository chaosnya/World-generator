package xyz.yggdrazil

import xyz.yggdrazil.midgard.demo.DemoApp
import xyz.yggdrazil.midgard.map.examples.TestGraphImpl
import xyz.yggdrazil.midgard.map.voronoi.VoronoiGraph
import xyz.yggdrazil.midgard.math.fortune.Voronoi
import java.util.*

/**
 * Created by Alexandre Mommers on 28/09/2016.
 */
fun main(args: Array<String>) {
    DemoApp.run()

    /*
    val bounds = 500
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
            g?.drawImage(img, 25, 35, null)
        }
    }

    frame.title = "java fortune"
    frame.isVisible = true
    frame.setSize(img.width + 50, img.height + 50)
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE*/
}


fun createVoronoiGraph(bounds: Int, numSites: Int, numLloydRelaxations: Int, seed: Long): VoronoiGraph {
    val r = Random(seed)

    //make the intial underlying voronoi structure
    val v = Voronoi.generate(numSites, bounds.toDouble(), bounds.toDouble(), r)

    //assemble the voronoi strucutre into a usable graph object representing a map
    val graph = TestGraphImpl(v, numLloydRelaxations, r)

    return graph
}