package xyz.yggdrazil.delaunay.geom

/**

 * @author Connor
 */

fun closeEnough(d1: Double, d2: Double, diff: Double): Boolean {
    return Math.abs(d1 - d2) <= diff
}

