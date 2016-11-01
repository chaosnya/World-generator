package xyz.yggdrazil.midgard.demo.fortune

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import xyz.yggdrazil.midgard.math.fortune.Voronoi
import java.util.*

/**
 * Created by Alexandre Mommers on 28/10/16.
 */
class FortuneRender(val context: GraphicsContext) {

    data class Settings(var showVoronoi: Boolean = true,
                        var showDelaunay: Boolean = false)

    val settings = Settings()

    fun draw(voronoi: Voronoi, width: Double, height: Double) {
        context.clearRect(.0, .0, width, height)

        if (settings.showVoronoi) {
            drawVoronoi(voronoi, width, height)
        }

        if (settings.showDelaunay) {
            drawDelaunay(voronoi, width, height)
        }
    }

    private fun drawDelaunay(voronoi: Voronoi, width: Double, height: Double) {

        context.stroke = Color.WHITE

        voronoi.siteCoords()
                .flatMap {
                    voronoi.delaunayLinesForSite(it)
                }
                .forEach { line ->
                    context.strokeLine(
                            rasterize(line.p0.x, width, voronoi.plotBounds.width),
                            rasterize(line.p0.y, height, voronoi.plotBounds.height),

                            rasterize(line.p1.x, width, voronoi.plotBounds.width),
                            rasterize(line.p1.y, height, voronoi.plotBounds.height)
                    )
                }
    }

    private fun drawVoronoi(voronoi: Voronoi, width: Double, height: Double) {
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