package xyz.yggdrazil.midgard.map.voronoi

import xyz.yggdrazil.midgard.math.geometry.Point
import java.util.*

/**
 * @author Connor
 */
class Corner {

    var touches = ArrayList<Center>() //good
    var adjacent = ArrayList<Corner>() //good
    var protrudes = ArrayList<Edge>()
    var loc: Point? = null
    var index: Int = 0
    var border: Boolean = false
    var elevation: Double = 0.toDouble()
    var water: Boolean = false
    var ocean: Boolean = false
    var coast: Boolean = false
    var downslope: Corner? = null
    var river: Int = 0
    var moisture: Double = 0.toDouble()
}
