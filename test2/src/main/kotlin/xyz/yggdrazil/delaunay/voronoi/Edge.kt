package xyz.yggdrazil.delaunay.voronoi

import xyz.yggdrazil.delaunay.geom.Point

/**
 * @author Connor
 */
class Edge {

    var index: Int = 0
    var d0: Center? = null
    var d1: Center? = null  // Delaunay edge
    var v0: Corner? = null
    var v1: Corner? = null  // Voronoi edge
    var midpoint: Point? = null  // halfway between v0,v1
    var river: Int = 0

    fun setVornoi(v0: Corner, v1: Corner) {
        this.v0 = v0
        this.v1 = v1
        midpoint = Point((v0.loc!!.x + v1.loc!!.x) / 2, (v0.loc!!.y + v1.loc!!.y) / 2)
    }
}
