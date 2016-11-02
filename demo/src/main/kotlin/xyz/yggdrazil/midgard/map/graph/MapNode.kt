package xyz.yggdrazil.midgard.map.graph

import xyz.yggdrazil.midgard.math.geometry.Point
import java.util.*

/**
 * Created by Alexandre Mommers on 03/11/2016.
 */
open class MapNode : HashSet<Point>() {
    val neighbours = HashSet<MapNode>()
}