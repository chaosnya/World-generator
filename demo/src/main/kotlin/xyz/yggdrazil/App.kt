package xyz.yggdrazil

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
 * Created by Alexandre Mommers on 28/09/2016.
 */
fun main(args: Array<String>) {
    DemoApp.run()
}
