package xyz.yggdrazil.math

/**

 * @author Connor
 */

fun closeEnough(d1: Double, d2: Double, diff: Double): Boolean {
    return Math.abs(d1 - d2) <= diff
}

