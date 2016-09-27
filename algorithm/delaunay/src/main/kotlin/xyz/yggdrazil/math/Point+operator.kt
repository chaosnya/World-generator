package xyz.yggdrazil.math

/**
 * Created by Alexandre Mommers on 19/09/16.
 */
/**
 * Subtract.

 * @param p the other Point
 * *
 * @return a new Point = this - p
 */
fun Point.subtract(p: Point): Point {
    val len = dimCheck(p)
    val coords = DoubleArray(len)
    for (i in 0..len - 1)
        coords[i] = this.coordinates[i] - p.coordinates[i]
    return Point(*coords)
}

/* Pnts as simplices */

/**
 * Add.
 * @param p the other Point
 * *
 * @return a new Point = this + p
 */
fun Point.add(p: Point): Point {
    val len = dimCheck(p)
    val coords = DoubleArray(len)
    for (i in 0..len - 1)
        coords[i] = this.coordinates[i] + p.coordinates[i]
    return Point(*coords)
}

/**
 * Sub.
 * @param p the other Point
 * *
 * @return a new Point = this * p
 */
fun Point.sub(p: Point): Point {
    val len = dimCheck(p)
    val coords = DoubleArray(len)
    for (i in 0..len - 1)
        coords[i] = this.coordinates[i] - p.coordinates[i]
    return Point(*coords)
}

/**
 * Mul.
 * @param p the other Point
 * *
 * @return a new Point = this * p
 */
fun Point.mul(p: Point): Point {
    val len = dimCheck(p)
    val coords = DoubleArray(len)
    for (i in 0..len - 1)
        coords[i] = this.coordinates[i] * p.coordinates[i]
    return Point(*coords)
}

/**
 * Add.
 * @param s value to add
 * *
 * @return a new Point = this - s
 */
fun Point.add(s: Double): Point {
    val len = this.coordinates.size
    val coords = DoubleArray(len)
    for (i in 0..len - 1)
        coords[i] = this.coordinates[i] + s
    return Point(*coords)
}

/**
 * Sub.
 * @param s value to subdivise
 * *
 * @return a new Point = this - s
 */
fun Point.sub(s: Double): Point {
    val len = this.coordinates.size
    val coords = DoubleArray(len)
    for (i in 0..len - 1)
        coords[i] = this.coordinates[i] - s
    return Point(*coords)
}

/**
 * Mul.
 * @param s value to multiplie
 * *
 * @return a new Point = this * s
 */
fun Point.mul(s: Double): Point {
    val len = this.coordinates.size
    val coords = DoubleArray(len)
    for (i in 0..len - 1)
        coords[i] = this.coordinates[i] * s
    return Point(*coords)
}

/**
 * Div.

 * @param p the other Point
 * *
 * @return a new Point = this / p
 */
operator fun Point.div(p: Point): Point {
    val len = dimCheck(p)
    val coords = DoubleArray(len)
    for (i in 0..len - 1)
        coords[i] = this.coordinates[i] / p.coordinates[i]
    return Point(*coords)
}

/**
 * Div.
 * @param s value to divide
 * *
 * @return a new Point = this / s
 */
operator fun Point.div(s: Double): Point {
    val inv = 1.0f / s

    val len = this.coordinates.size
    val coords = DoubleArray(len)
    for (i in 0..len - 1)
        coords[i] = this.coordinates[i] * inv
    return Point(*coords)
}
