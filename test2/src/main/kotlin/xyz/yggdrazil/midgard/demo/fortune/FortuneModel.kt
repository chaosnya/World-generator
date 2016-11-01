package xyz.yggdrazil.midgard.demo.fortune

import xyz.yggdrazil.fortune.Voronoi
import xyz.yggdrazil.math.geometry.Point
import java.util.*

/**
 * Created by Alexandre Mommers on 18/10/16.
 */
class FortuneModel {
    data class Settings(var sites: Int = 100,
                        var lloydRelaxations: Int = 2,
                        var seed: Long = System.nanoTime())

    val settings = Settings()
    val voronoi: Voronoi
        get() {
            val random = Random(settings.seed)
            var voronoi = Voronoi.generate(
                    settings.sites,
                    1000.toDouble(), 1000.toDouble(),
                    random
            )

            for (i in 1..settings.lloydRelaxations) {
                val points = voronoi.siteCoords()
                lloydRelaxation(points, voronoi)
                voronoi = Voronoi(points, voronoi.plotBounds)
            }

            return voronoi
        }


    private fun lloydRelaxation(points: ArrayList<Point>, voronoi: Voronoi) {
        for (p in points) {
            val region = voronoi.region(p)
            var x = 0.0
            var y = 0.0
            for (c in region) {
                x += c.x
                y += c.y
            }
            x /= region.size.toDouble()
            y /= region.size.toDouble()
            p.x = x
            p.y = y
        }

    }
}