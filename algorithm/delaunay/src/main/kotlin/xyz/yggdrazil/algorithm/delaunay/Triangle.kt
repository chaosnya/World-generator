package xyz.yggdrazil.algorithm.delaunay

/*
 * Copyright (c) 2007 by L. Paul Chew.
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

import xyz.yggdrazil.math.Point
import java.util.*

/**
 * A Triangle is an immutable Set of exactly three Point.
 *
 *
 * All Set operations are available. Individual vertices can be accessed via
 * iterator() and also via triangle.get(index).
 *
 *
 * Note that, even if two triangles have the same vertex set, they are
 * *different* triangles. Methods equals() and hashCode() are consistent with
 * this rule.

 * @author Paul Chew
 * *
 *
 *
 * *         Created December 2007. Replaced general simplices with geometric triangle.
 * @param collection a Collection holding the Simplex vertices
 *
 * @throws IllegalArgumentException if there are not three distinct vertices
 */
class Triangle(collection: Collection<Point>) : ArraySet<Point>(collection) {
    private val idNumber: Int                   // The id number
    private var circumcenter: Point? = null        // The triangle's circumcenter

    /**
     * @param vertices the vertices of the Triangle.
     * *
     * @throws IllegalArgumentException if there are not three distinct vertices
     */
    constructor(vararg vertices: Point) : this(Arrays.asList(*vertices)) {
    }

    init {
        idNumber = idGenerator++
        if (this.size != 3)
            throw IllegalArgumentException("Triangle must have 3 vertices")
    }

    override fun toString(): String {
        if (!moreInfo) return "Triangle" + idNumber
        return "Triangle" + idNumber + super.toString()
    }

    /**
     * Get arbitrary vertex of this triangle, but not any of the bad vertices.

     * @param badVertices one or more bad vertices
     * *
     * @return a vertex of this triangle, but not one of the bad vertices
     * *
     * @throws NoSuchElementException if no vertex found
     */
    fun getVertexButNot(vararg badVertices: Point): Point {
        val bad = Arrays.asList(*badVertices)
        for (v in this) if (!bad.contains(v)) return v
        throw NoSuchElementException("No vertex found")
    }

    /**
     * True iff triangles are neighbors. Two triangles are neighbors if they
     * share a facet.

     * @param triangle the other Triangle
     * *
     * @return true iff this Triangle is a neighbor of triangle
     */
    fun isNeighbor(triangle: Triangle): Boolean {
        var count = 0
        for (vertex in this)
            if (!triangle.contains(vertex)) count++
        return count == 1
    }

    /**
     * Report the facet opposite vertex.

     * @param vertex a vertex of this Triangle
     * *
     * @return the facet opposite vertex
     * *
     * @throws IllegalArgumentException if the vertex is not in triangle
     */
    fun facetOpposite(vertex: Point): ArraySet<Point> {
        val facet = ArraySet(this)
        if (!facet.remove(vertex))
            throw IllegalArgumentException("Vertex not in triangle")
        return facet
    }

    /**
     * @return the triangle's circumcenter
     */
    fun getCircumcenter(): Point {
        val circumcenter = circumcenter
        if (circumcenter == null) {
            this.circumcenter = Point.circumcenter(this.toTypedArray())
        }
        return this.circumcenter!!
    }

    /* The following two methods ensure that a Triangle is immutable */

    override fun add(vertex: Point?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun iterator(): MutableIterator<Point> {
        val iterator = super.iterator()
        return object : MutableIterator<Point> {
            private val it = iterator

            override fun hasNext(): Boolean {
                return it.hasNext()
            }

            override fun next(): Point {
                return it.next()
            }

            override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }

    /* The following two methods ensure that all triangles are different. */

    override fun hashCode(): Int {
        return (idNumber xor idNumber.ushr(32)).toInt()
    }

    override fun equals(o: Any?): Boolean {
        return this === o
    }

    companion object {

        var moreInfo = false // True iff more info in toString
        private var idGenerator = 0     // Used to create id numbers
    }

}