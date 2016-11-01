package xyz.yggdrazil.midgard.map.voronoi

import xyz.yggdrazil.midgard.math.geometry.Point
import java.util.*

/**
 * @author Connor
 */
class Center(var loc: Point) {

    var index: Int = 0
    var corners = ArrayList<Corner>()//good
    var neighbors = ArrayList<Center>()//good
    var borders = ArrayList<Edge>()
    var border: Boolean = false
    var ocean: Boolean = false
    var water: Boolean = false
    var coast: Boolean = false
    var elevation: Double = 0.toDouble()
    var moisture: Double = 0.toDouble()
    var biome: Enum<*>? = null
    var area: Double = 0.toDouble()


}
