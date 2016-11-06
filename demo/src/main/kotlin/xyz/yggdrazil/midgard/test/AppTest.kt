package xyz.yggdrazil.midgard.test

import xyz.yggdrazil.midgard.demo.DemoApp
import xyz.yggdrazil.midgard.map.render.BufferedImageMapRenderer
import xyz.yggdrazil.midgard.map.voronoi.VoronoiMap
import xyz.yggdrazil.midgard.map.voronoi.VoronoiMapSettings
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
 * Created by Alexandre Mommers on 06/11/16.
 */
fun main(args: Array<String>) {

    val mapSettings = VoronoiMapSettings()
    val map = VoronoiMap(mapSettings)
    val renderer = BufferedImageMapRenderer(map)
    val image = renderer.render()
    //val img = createVoronoiGraph(bounds, numSites, numLloydRelxations, seed).createMap()
    showImage(image)
    writeImageAsFile(image, mapSettings.seed, mapSettings.sites, mapSettings.lloydRelaxations)
    
}

fun writeImageAsFile(image: BufferedImage, seed: Long, numSites: Int, numLloydRelxations: Int) {

    val file = File(String.format("output/seed-%s-sites-%d-lloyds-%d.png", seed, numSites, numLloydRelxations))
    file.mkdirs()
    ImageIO.write(image, "PNG", file)
}

fun showImage(image: BufferedImage) {
    val frame = object : JFrame() {
        override fun paint(g: Graphics?) {
            g?.drawImage(image, 0, 0, null)
        }
    }

    frame.title = "java fortune"
    frame.isVisible = true
    frame.setSize(image.width, image.height)
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