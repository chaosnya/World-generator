package xyz.yggdrazil.delaunay.voronoi.nodename.as3delaunay

import xyz.yggdrazil.delaunay.geom.Point
import xyz.yggdrazil.delaunay.geom.Rectangle

import java.util.ArrayList

class SiteList : IDisposable {

    private val sites = ArrayList<Site>()
    private var currentIndex = 0
    private var sorted = false


    override fun dispose() {
        if (sites != null) {
            for (site in sites) {
                site.dispose()
            }
            sites.clear()
        }
    }

    fun push(site: Site): Int {
        sorted = false
        sites.add(site)
        return sites.size
    }

    val length: Int
        get() = sites.size

    operator fun next(): Site? {
        if (!sorted) {
            throw Error("SiteList::next():  sites have not been sorted")
        }
        if (currentIndex < sites.size) {
            return sites[currentIndex++]
        } else {
            return null
        }
    }

    // here's where we assume that the sites have been sorted on y:
    val sitesBounds: Rectangle
        get() {
            if (!sorted) {
                Site.sortSites(sites)
                currentIndex = 0
                sorted = true
            }
            var xmin: Double
            var xmax: Double
            val ymin: Double
            val ymax: Double
            if (sites.isEmpty()) {
                return Rectangle(0.0, 0.0, 0.0, 0.0)
            }
            xmin = java.lang.Double.MAX_VALUE
            xmax = java.lang.Double.MIN_VALUE
            for (site in sites) {
                if (site._x < xmin) {
                    xmin = site._x
                }
                if (site._x > xmax) {
                    xmax = site._x
                }
            }
            ymin = sites[0]._y
            ymax = sites[sites.size - 1]._y

            return Rectangle(xmin, ymin, xmax - xmin, ymax - ymin)
        }

    /*public ArrayList<Color> siteColors(referenceImage:BitmapData = null)
     {
     var colors:Vector.<uint> = new Vector.<uint>();
     for each (var site:Site in sites)
     {
     colors.push(referenceImage ? referenceImage.getPixel(site.x, site.y) : site.color);
     }
     return colors;
     }*/
    fun siteCoords(): ArrayList<Point> {
        val coords = ArrayList<Point>()
        for (site in sites) {
            coords.add(site.coord!!)
        }
        return coords
    }

    /**
     * @return the largest circle centered at each site that fits in its region;
     * * if the region is infinite, return a circle of radius 0.
     */
    fun circles(): ArrayList<Circle> {
        val circles = ArrayList<Circle>()
        for (site in sites) {
            var radius = 0.0
            val nearestEdge = site.nearestEdge()

            //!nearestEdge.isPartOfConvexHull() && (radius = nearestEdge.sitesDistance() * 0.5);
            if (!nearestEdge.isPartOfConvexHull) {
                radius = nearestEdge.sitesDistance() * 0.5
            }
            circles.add(Circle(site._x, site._y, radius))
        }
        return circles
    }

    fun regions(plotBounds: Rectangle): ArrayList<ArrayList<Point>> {
        val regions = ArrayList<ArrayList<Point>>()
        for (site in sites) {
            regions.add(site.region(plotBounds)!!)
        }
        return regions
    }

}