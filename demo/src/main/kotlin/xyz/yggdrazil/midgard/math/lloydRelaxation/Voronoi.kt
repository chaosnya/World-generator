package xyz.yggdrazil.midgard.math.lloydRelaxation

import xyz.yggdrazil.midgard.math.fortune.Voronoi

/**
 * Created by Alexandre Mommers on 04/11/16.
 */

fun Voronoi.relax() : Voronoi {
    val points = siteCoords()

    for (point in points) {
        val region = region(point)
        var x = .0
        var y = .0
        for (c in region) {
            x += c.x
            y += c.y
        }
        x /= region.size.toDouble()
        y /= region.size.toDouble()
        point.x = x
        point.y = y
    }

    return Voronoi(points, plotBounds)
}