package xyz.yggdrazil.midgard.map.voronoi

import xyz.yggdrazil.midgard.map.graph.MapSettings

/**
 * Created by Alexandre Mommers on 03/11/2016.
 */
class VoronoiMapSettings(
        width: Int = 1000,
        height: Int = 1000,
        var sites: Int = 2000,
        var lloydRelaxations: Int = 2,
        var seed: Long = System.nanoTime()
) : MapSettings(width, height) {


}