package xyz.yggdrazil.math

/*
 * Copyright (c) 2005, 2007 by L. Paul Chew.
 *
 * Permission is hereby granted, without written agreement and without
 * license or royalty fees, to use, copy, modify, and distribute this
 * software and its documentation for any purpose, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

/**
 * Point2s in Euclidean space, implemented as double[].
 *
 *
 * Includes simple geometric operations.
 * Uses matrices; a matrix is represented as an array of Pnts.
 * Uses simplices; a simplex is represented as an array of Pnts.

 * @author Paul Chew
 * *
 *
 *
 * *         Created July 2005.  Derived from an earlier, messier version.
 * *
 *
 *
 * *         Modified Novemeber 2007.  Minor clean up.
 *
 * @param coords the coordinates
 */
open class Point(vararg coords: Double) {

    // The Point's coordinates
    internal val coordinates: DoubleArray

    var x: Double
        get() = coordinates[0]
        set(value) {
            coordinates[0] = value
        }

    var y: Double
        get() = coordinates[1]
        set(value) {
            coordinates[1] = value
        }

    var z: Double
        get() = coordinates[2]
        set(value) {
            coordinates[2] = value
        }

    init {
        // Copying is done here to ensure that Point's coords cannot be altered.
        // This is necessary because the double... notation actually creates a
        // constructor with double[] as its argument.
        coordinates = DoubleArray(coords.size)
        System.arraycopy(coords, 0, coordinates, 0, coords.size)
    }

    override fun toString(): String {
        if (coordinates.size == 0) return "Point()"
        var result = "Point(" + coordinates[0]
        for (i in 1..coordinates.size - 1)
            result = result + "," + coordinates[i]
        result = result + ")"
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Point) return false
        if (this.coordinates.size != other.coordinates.size) return false
        for (i in this.coordinates.indices)
            if (this.coordinates[i] != other.coordinates[i]) return false
        return true
    }

    override fun hashCode(): Int {
        var hash = 0
        for (c in this.coordinates) {
            val bits = java.lang.Double.doubleToLongBits(c)
            hash = 31 * hash xor (bits xor (bits shr 32)).toInt()
        }
        return hash
    }

    /**
     * @return the specified coordinate of this Point
     * *
     * @throws ArrayIndexOutOfBoundsException for bad coordinate
     */
    fun coord(i: Int): Double {
        return this.coordinates[i]
    }

    /**
     * @return this Point's dimension.
     */
    fun dimension(): Int {
        return coordinates.size
    }

    /**
     * Check that dimensions match.

     * @param p the Point to check (against this Point)
     * *
     * @return the dimension of the Points
     * *
     * @throws IllegalArgumentException if dimension fail to match
     */
    fun dimCheck(p: Point): Int {
        val len = this.coordinates.size
        if (len != p.coordinates.size)
            throw IllegalArgumentException("Dimension mismatch")
        return len
    }

/* Points as matrices */

    /**
     * Create a new Point by adding additional coordinates to this Point.

     * @param coords the new coordinates (added on the right end)
     * *
     * @return a new Point with the additional coordinates
     */
    fun extend(vararg coords: Double): Point {
        val result = DoubleArray(coordinates.size + coords.size)
        System.arraycopy(coordinates, 0, result, 0, coordinates.size)
        System.arraycopy(coords, 0, result, coordinates.size, coords.size)
        return Point(*result)
    }

    /**
     * Dot product.

     * @param p the other Point
     * *
     * @return dot product of this Point and p
     */
    fun dot(p: Point): Double {
        val len = dimCheck(p)
        var sum = 0.0
        for (i in 0..len - 1)
            sum += this.coordinates[i] * p.coordinates[i]
        return sum
    }

    /**
     * Magnitude (as a vector).

     * @return the Euclidean length of this vector
     */
    fun magnitude(): Double {
        return Math.sqrt(this.dot(this))
    }

    /**
     * Angle (in radians) between two Points (treated as vectors).

     * @param p the other Point
     * *
     * @return the angle (in radians) between the two Points
     */
    fun angle(p: Point): Double {
        return Math.acos(this.dot(p) / (this.magnitude() * p.magnitude()))
    }

    /**
     * Perpendicular bisector of two Points.
     * Works in any dimension.  The coefficients are returned as a Point of one
     * higher dimension (e.g., (A,B,C,D) for an equation of the form
     * Ax + By + Cz + D = 0).

     * @param Point the other Point
     * *
     * @return the coefficients of the perpendicular bisector
     */
    fun bisector(Point: Point): Point {
        dimCheck(Point)
        val diff = this.subtract(Point)
        val sum = this.add(Point)
        val dot = diff.dot(sum)
        return diff.extend(-dot / 2)
    }

    /**
     * Relation between this Point and a simplex (represented as an array of
     * Points). Result is an array of signs, one for each vertex of the simplex,
     * indicating the relation between the vertex, the vertex's opposite facet,
     * and this Point.
     *
     *
     *
     * -1 means Point is on same side of facet
     * 0 means Point is on the facet
     * +1 means Point is on opposite side of facet
     *

     * @param simplex an array of Points representing a simplex
     * *
     * @return an array of signs showing relation between this Point and simplex
     * *
     * @throws IllegalArgumentExcpetion if the simplex is degenerate
     */
    fun relation(simplex: Array<Point>): IntArray {
        /* In 2D, we compute the cross of this matrix:
         *    1   1   1   1
         *    p0  a0  b0  c0
         *    p1  a1  b1  c1
         * where (a, b, c) is the simplex and p is this Point. The result is a
         * vector in which the first coordinate is the signed area (all signed
         * areas are off by the same constant factor) of the simplex and the
         * remaining coordinates are the *negated* signed areas for the
         * simplices in which p is substituted for each of the vertices.
         * Analogous results occur in higher dimensions.
         */
        val dim = simplex.size - 1
        if (this.dimension() != dim)
            throw IllegalArgumentException("Dimension mismatch")

        /* Create and load the matrix */
        val matrix = arrayListOf<Point>()
        /* First row */
        val coords = DoubleArray(dim + 2)
        for (j in coords.indices) coords[j] = 1.0
        matrix.add(Point(*coords))
        /* Other rows */
        for (i in 0..dim - 1) {
            coords[0] = this.coordinates[i]
            for (j in simplex.indices)
                coords[j + 1] = simplex[j].coordinates[i]
            matrix.add(Point(*coords))
        }

        /* Compute and analyze the vector of areas/volumes/contents */
        val vector = cross(matrix.toTypedArray())
        val content = vector.coordinates[0]
        val result = IntArray(dim + 1)
        for (i in result.indices) {
            val value = vector.coordinates[i + 1]
            if (Math.abs(value) <= 1.0e-6 * Math.abs(content))
                result[i] = 0
            else if (value < 0)
                result[i] = -1
            else
                result[i] = 1
        }
        if (content < 0) {
            for (i in result.indices)
                result[i] = -result[i]
        }
        if (content == 0.0) {
            for (i in result.indices)
                result[i] = Math.abs(result[i])
        }
        return result
    }

    /**
     * Test if this Point is outside of simplex.

     * @param simplex the simplex (an array of Points)
     * *
     * @return simplex Point that "witnesses" outsideness (or null if not outside)
     */
    fun isOutside(simplex: Array<Point>): Point? {
        val result = this.relation(simplex)
        for (i in result.indices) {
            if (result[i] > 0) return simplex[i]
        }
        return null
    }

    /**
     * Test if this Point is on a simplex.

     * @param simplex the simplex (an array of Points)
     * *
     * @return the simplex Point that "witnesses" on-ness (or null if not on)
     */
    fun isOn(simplex: Array<Point>): Point? {
        val result = this.relation(simplex)
        var witness: Point? = null
        for (i in result.indices) {
            if (result[i] == 0)
                witness = simplex[i]
            else if (result[i] > 0) return null
        }
        return witness
    }

    /**
     * Test if this Point is inside a simplex.

     * @param simplex the simplex (an arary of Points)
     * *
     * @return true iff this Point is inside simplex.
     */
    fun isInside(simplex: Array<Point>): Boolean {
        val result = this.relation(simplex)
        for (r in result) if (r >= 0) return false
        return true
    }

    /**
     * Test relation between this Point and circumcircle of a simplex.

     * @param simplex the simplex (as an array of Points)
     * *
     * @return -1, 0, or +1 for inside, on, or outside of circumcircle
     */
    fun vsCircumcircle(simplex: Array<Point>): Int {
        val matrix = arrayListOf<Point>()
        for (i in simplex.indices)
            matrix.add(simplex[i].extend(1.0, simplex[i].dot(simplex[i])))
        matrix.add(this.extend(1.0, this.dot(this)))

        val d = determinant(matrix.toTypedArray())
        var result = if (d < 0) -1 else if (d > 0) +1 else 0
        if (content(simplex) < 0) result = -result
        return result
    }

    companion object {

        /**
         * Create a String for a matrix.

         * @param matrix the matrix (an array of Points)
         * *
         * @return a String represenation of the matrix
         */
        fun toString(matrix: Array<Point>): String {
            val buf = StringBuilder("{")
            for (row in matrix) buf.append(" " + row)
            buf.append(" }")
            return buf.toString()
        }

        /**
         * Compute the determinant of a matrix (array of Points).
         * This is not an efficient implementation, but should be adequate
         * for low dimension.

         * @param matrix the matrix as an array of Points
         * *
         * @return the determinnant of the input matrix
         * *
         * @throws IllegalArgumentException if dimensions are wrong
         */
        fun determinant(matrix: Array<Point>): Double {
            if (matrix.size != matrix[0].dimension())
                throw IllegalArgumentException("Matrix is not square")
            val columns = BooleanArray(matrix.size)
            for (i in matrix.indices) columns[i] = true
            try {
                return determinant(matrix, 0, columns)
            } catch (e: ArrayIndexOutOfBoundsException) {
                throw IllegalArgumentException("Matrix is wrong shape")
            }

        }

        /**
         * Compute the determinant of a submatrix specified by starting row
         * and by "active" columns.

         * @param matrix  the matrix as an array of Points
         * *
         * @param row     the starting row
         * *
         * @param columns a boolean array indicating the "active" columns
         * *
         * @return the determinant of the specified submatrix
         * *
         * @throws ArrayIndexOutOfBoundsException if dimensions are wrong
         */
        private fun determinant(matrix: Array<Point>, row: Int, columns: BooleanArray): Double {
            if (row == matrix.size) return 1.0
            var sum = 0.0
            var sign = 1
            for (col in columns.indices) {
                if (!columns[col]) continue
                columns[col] = false
                sum += sign.toDouble() * matrix[row].coordinates[col] *
                        determinant(matrix, row + 1, columns)
                columns[col] = true
                sign = -sign
            }
            return sum
        }

        /* Points as vectors */

        /**
         * Compute generalized cross-product of the rows of a matrix.
         * The result is a Point perpendicular (as a vector) to each row of
         * the matrix.  This is not an efficient implementation, but should
         * be adequate for low dimension.

         * @param matrix the matrix of Points (one less row than the Point dimension)
         * *
         * @return a Point perpendicular to each row Point
         * *
         * @throws IllegalArgumentException if matrix is wrong shape
         */
        fun cross(matrix: Array<Point>): Point {
            val len = matrix.size + 1
            if (len != matrix[0].dimension())
                throw IllegalArgumentException("Dimension mismatch")
            val columns = BooleanArray(len)
            for (i in 0..len - 1) columns[i] = true
            val result = DoubleArray(len)
            var sign = 1
            try {
                for (i in 0..len - 1) {
                    columns[i] = false
                    result[i] = sign * determinant(matrix, 0, columns)
                    columns[i] = true
                    sign = -sign
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
                throw IllegalArgumentException("Matrix is wrong shape")
            }

            return Point(*result)
        }

        /**
         * Determine the signed content (i.e., area or volume, etc.) of a simplex.

         * @param simplex the simplex (as an array of Points)
         * *
         * @return the signed content of the simplex
         */
        fun content(simplex: Array<Point>): Double {
            val matrix = arrayListOf<Point>()
            for (i in simplex.indices)
                matrix.add(simplex[i].extend(1.0))
            var fact = 1
            for (i in 1..matrix.size - 1) fact = fact * i
            return determinant(matrix.toTypedArray()) / fact
        }

        /**
         * Circumcenter of a simplex.

         * @param simplex the simplex (as an array of Points)
         * *
         * @return the circumcenter (a Point) of simplex
         */
        fun circumcenter(simplex: Array<Point>): Point {
            val dim = simplex[0].dimension()
            if (simplex.size - 1 != dim)
                throw IllegalArgumentException("Dimension mismatch")
            val matrix = arrayListOf<Point>()
            for (i in 0..dim - 1)
                matrix.add(simplex[i].bisector(simplex[i + 1]))
            val hCenter = cross(matrix.toTypedArray())      // Center in homogeneous coordinates
            val last = hCenter.coordinates[dim]
            val result = DoubleArray(dim)
            for (i in 0..dim - 1) result[i] = hCenter.coordinates[i] / last
            return Point(*result)
        }

        /**
         * Main program (used for testing).
         */
        @JvmStatic fun main(args: Array<String>) {
            val p = Point(1.0, 2.0, 3.0)
            println("Point created: " + p)
            val matrix1 = arrayOf(Point(1.0, 2.0), Point(3.0, 4.0))
            val matrix2 = arrayOf(Point(7.0, 0.0, 5.0), Point(2.0, 4.0, 6.0), Point(3.0, 8.0, 1.0))
            print("Results should be -2 and -288: ")
            println("${determinant(matrix1)} ${determinant(matrix2)}")
            val p1 = Point(1.0, 1.0)
            val p2 = Point(-1.0, 1.0)
            println("Angle between " + p1 + " and " + p2 + ": " + p1.angle(p2))

            println("${p1} ${determinant(matrix2)}")
            println("${p1} subtract ${p2}: ${p1.subtract(p2)}")
            val v0 = Point(0.0, 0.0)
            val v1 = Point(1.0, 1.0)
            val v2 = Point(2.0, 2.0)
            val vs = arrayOf(v0, Point(0.0, 1.0), Point(1.0, 0.0))
            val vp = Point(.1, .1)
            println("${vp} isInside ${toString(vs)}: ${vp.isInside(vs)}")
            println("${v1} isInside ${toString(vs)}: ${v1.isInside(vs)}")
            println("${vp} vsCircumcircle  ${toString(vs)}: ${vp.vsCircumcircle(vs)}")
            println("${v1} vsCircumcircle  ${toString(vs)}: ${v1.vsCircumcircle(vs)}")
            println("${v2} vsCircumcircle ${toString(vs)}: ${v2.vsCircumcircle(vs)}")
            println("Circumcenter of " + toString(vs) + " is " + circumcenter(vs))
        }
    }

    fun toVector(): Vector {
        val dimension = dimension()
        when (dimension()) {
            in 0..1 -> {
                throw IllegalArgumentException("Dimension mismatch")
            }
            2 -> {
                return Vector(x, y)
            }
            else -> {
                return Vector(x, y, y)
            }

        }
    }
}