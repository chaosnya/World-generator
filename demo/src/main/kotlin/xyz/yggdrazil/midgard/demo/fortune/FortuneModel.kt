package xyz.yggdrazil.midgard.demo.fortune

import xyz.yggdrazil.midgard.math.fortune.Voronoi
import xyz.yggdrazil.midgard.math.geometry.Point
import xyz.yggdrazil.midgard.math.lloydRelaxation.relax
import java.util.*

/**
 * Created by Alexandre Mommers on 18/10/16.
 */
class FortuneModel {
    data class Settings(var sites: Int = 1000,
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
                voronoi = voronoi.relax()
            }

            return voronoi
        }
}