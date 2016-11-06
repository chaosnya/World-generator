package xyz.yggdrazil.midgard.map.render

import xyz.yggdrazil.midgard.map.graph.MapGraph
import xyz.yggdrazil.midgard.map.graph.MapNode
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.*

/**
 * Created by Alexandre Mommers on 06/11/16.
 */
class BufferedImageMapRenderer(val map: MapGraph<*, *>) {

    fun render(): BufferedImage {
        //Create image
        val size = Math.max(map.settings.height, map.settings.width)
        val image = BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR)
        val graphics = image.createGraphics()

        //Draw map
        drawMap(graphics)

        return image
    }

    private fun drawMap(graphics: Graphics2D) {
        map.forEach { node ->
            drawNode(node, graphics)
        }
    }

    private fun drawNode(node: MapNode, graphics: Graphics2D) {
        if (node.size == 0) {
            return
        }

        graphics.color = randomColor()
        val xCoordinate = IntArray(node.size)
        val yCoordinate = IntArray(node.size)
        node.forEachIndexed { index, point -> xCoordinate[index] = point.x.toInt() }
        node.forEachIndexed { index, point -> yCoordinate[index] = point.y.toInt() }
        graphics.fillPolygon(xCoordinate, yCoordinate, node.size)

    }

    private fun randomColor(): Color {
        val random = Random()
        return Color(
                random.nextInt(255),
                random.nextInt(255),
                random.nextInt(255),
                255
        )
    }
}
