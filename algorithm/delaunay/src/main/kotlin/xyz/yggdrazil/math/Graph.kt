package xyz.yggdrazil.math

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

import xyz.yggdrazil.algorithm.delaunay.ArraySet
import java.util.*

/**
 * Straightforward undirected graph implementation.
 * Nodes are generic type N.
 * @author Paul Chew
 *
 * Created November, December 2007.  For use in Delaunay/Voronoi code.
 */
open class Graph<N> {

    // Node -> adjacent nodes
    private val theNeighbors = HashMap<N, MutableSet<N>>()

    /**
     * Add a node.  If node is already in graph then no change.

     * @param node the node to add
     */
    fun add(node: N) {
        if (theNeighbors.containsKey(node)) return
        theNeighbors.put(node, ArraySet<N>())
    }

    /**
     * Add a link. If the link is already in graph then no change.

     * @param nodeA one end of the link
     * *
     * @param nodeB the other end of the link
     * *
     * @throws NullPointerException if either endpoint is not in graph
     */
    @Throws(NullPointerException::class)
    fun link(nodeA: N, nodeB: N) {
        theNeighbors[nodeA]?.add(nodeB)
        theNeighbors[nodeB]?.add(nodeA)
    }

    /**
     * Remove node and any links that use node. If node not in graph, nothing
     * happens.

     * @param node the node to remove.
     */
    fun remove(node: N) {
        if (!theNeighbors.containsKey(node)) return
        theNeighbors[node]?.let { neighbors ->
            for (neighbor in neighbors)
                theNeighbors[neighbor]?.remove(node)    // Remove "to" links
        }
        theNeighbors[node]?.clear()                 // Remove "from" links
        theNeighbors.remove(node)                      // Remove the node
    }

    /**
     * Remove the specified link. If link not in graph, nothing happens.

     * @param nodeA one end of the link
     * *
     * @param nodeB the other end of the link
     * *
     * @throws NullPointerException if either endpoint is not in graph
     */
    @Throws(NullPointerException::class)
    fun unlink(nodeA: N, nodeB: N) {
        theNeighbors[nodeA]?.remove(nodeB)
        theNeighbors[nodeB]?.remove(nodeA)
    }

    /**
     * Report all the neighbors of node.

     * @param node the node
     * *
     * @return the neighbors of node
     * *
     * @throws NullPointerException if node does not appear in graph
     */
    @Throws(NullPointerException::class)
    fun neighbors(node: N): Set<N> {
        return Collections.unmodifiableSet(theNeighbors[node])
    }

    /**
     * Returns an unmodifiable Set view of the nodes contained in this graph.
     * The set is backed by the graph, so changes to the graph are reflected in
     * the set.

     * @return a Set view of the graph's node set
     */
    fun nodeSet(): Set<N> {
        return Collections.unmodifiableSet(theNeighbors.keys)
    }

}
