package xyz.yggdrazil.gui.model.voronoi

import xyz.yggdrazil.algorithm.delaunay.Triangle
import xyz.yggdrazil.algorithm.delaunay.Triangulation
import xyz.yggdrazil.gui.model.Config
import xyz.yggdrazil.gui.model.Diagram
import xyz.yggdrazil.math.Point
import xyz.yggdrazil.math.surface.Plane
import java.util.*

/**
 * Graphics Panel for DelaunayAp.
 */
class VoronoiDiagram(private val config: Config) : Diagram<Site>() {
    private val random = Random()
    private val siteMatrix = ArrayList<Point>()
    val viewport = Plane(0.0, 0.0, 100.0, 100.0)

    /**
     * Re-initialize
     */
    fun reset() {
        polygons.remove()
        siteMatrix.clear()
    }

    companion object {
        private val initialSize = 10000     // Size of initial triangle
    }
    
    fun addRandomPoints() {

        for (i in 1..100) {
            val point = viewport.randomPointInside()
            siteMatrix.add(point)
        }

        compute()
    }

    fun removeRandomPoint() {

        for (i in 1..100) {
            val index = random.nextInt(siteMatrix.count())
            siteMatrix.remove(siteMatrix[index])
        }

        compute()
    }

    fun increaseCentroid() {
        config.centroid++

        compute()
    }

    fun reduceCentroid() {
        config.centroid = Math.max(0, config.centroid - 1)

        compute()
    }

    override fun compute(): List<Site> {

        val boundaries = viewport.edges()

        var siteMatrix = siteMatrix

        for (index in 1..config.centroid) {

            val sites = computeSites(siteMatrix)

            siteMatrix = ArrayList<Point>()
            for (site in sites) {
                val newSite = Site(site.origin)
                newSite.addAll(
                        site.clippingAlg(site, boundaries)
                )
                siteMatrix.add(newSite.centroid().toPoint())
            }
        }

        computeSites(siteMatrix).map { site ->
            val newSite = Site(site.origin)
            newSite.addAll(site.clippingAlg(site, boundaries))
            newSite
        }.let { sites ->
            polygons.clear()
            polygons.addAll(sites)
        }
        return polygons
    }

    /*********
     * this section is used to compute voronoi diagram
     * current algorithm used is the Bowyer–Watson one
     */

    private fun computeSites(siteMatrix: List<Point>): LinkedList<Site> {

        /* initialize algorithm with a large triangle */
        val initialTriangle = Triangle(
                Point(-initialSize.toDouble(), -initialSize.toDouble()),
                Point(initialSize.toDouble(), -initialSize.toDouble()),
                Point(0.toDouble(), initialSize.toDouble()))
        val delaunayTriangulation = Triangulation(initialTriangle)

        /* add all sites to compute diagram */
        for (site in siteMatrix) {
            delaunayTriangulation.delaunayPlace(site)
        }

        val sites = LinkedList<Site>()
        // Keep track of sites done and skip initial triangles sites
        val done = HashSet(initialTriangle)
        for (triangle in delaunayTriangulation) {
            for (site in triangle) {
                if (done.contains(site)) continue
                done.add(site)
                val list = delaunayTriangulation.surroundingTriangles(site, triangle)
                val site = Site(site)
                for (tri in list) {
                    site.add(tri.getCircumcenter().toVector())
                }
                sites.add(site)
            }
        }

        return sites
    }

    fun addSite(point: Point) {
        siteMatrix.add(point)

        compute()
    }
}
