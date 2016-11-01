package xyz.yggdrazil.midgard.demo.fortune

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import xyz.yggdrazil.midgard.math.fortune.Voronoi
import java.util.*

/**
 * Created by Alexandre Mommers on 28/10/16.
 */
class FortuneRender {

    data class Settings(var showVoronoi: Boolean = true,
                        var showDelaunay: Boolean = true)

    val settings = Settings()

    fun draw(context: GraphicsContext, voronoi: Voronoi, width: Double, height: Double) {

        context.stroke = Color.BLACK
        voronoi.siteCoords()
                .forEach { origin ->
                    val fillColor = Color.color(
                            nextColor(),
                            nextColor(),
                            nextColor())
                    context.fill = fillColor
                    context.beginPath()
                    val region = voronoi.region(origin)
                    context.moveTo(
                            rasterize(region.get(0).x, width, voronoi.plotBounds.width),
                            rasterize(region.get(0).y, height, voronoi.plotBounds.height)
                    )
                    region.forEach { point ->
                        context.lineTo(
                                rasterize(point.x, width, voronoi.plotBounds.width),
                                rasterize(point.y, height, voronoi.plotBounds.height)
                        )
                    }
                    context.closePath()
                    context.fill()
                    context.stroke()
                }
    }

    fun nextColor(): Double {
        val random = Random()
        return random.nextInt(255).toDouble() / 255.0
    }

    /**
     * rasterize point from a space to a viewport
     */
    fun rasterize(origin: Double, viewport: Double, bounds: Double, zoom: Double = 1.0): Double {
        return origin * viewport / bounds * zoom
    }
}