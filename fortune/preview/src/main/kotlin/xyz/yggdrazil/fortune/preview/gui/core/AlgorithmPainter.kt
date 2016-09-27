package xyz.yggdrazil.fortune.preview.gui.core

import xyz.yggdrazil.fortune.Algorithm
import xyz.yggdrazil.fortune.Delaunay
import xyz.yggdrazil.fortune.Voronoi
import xyz.yggdrazil.fortune.arc.AbstractArcNodeVisitor
import xyz.yggdrazil.fortune.arc.ArcNode
import xyz.yggdrazil.fortune.arc.ArcNodeWalker
import xyz.yggdrazil.fortune.events.CirclePoint
import xyz.yggdrazil.fortune.events.EventPoint
import xyz.yggdrazil.fortune.events.EventQueue
import java.util.*

class AlgorithmPainter(private val algorithm: Algorithm, private val config: Config, private var painter: Painter) {

    var width: Int = 0
    var height: Int = 0
    private val colorBackground = 0xffffff
    private val colorSweepline = 0xff0000
    private val colorSites = 0x000000
    private val colorSitesVisited = 0x666666
    private val colorSiteActive = 0xff0000
    private val colorCircleEventPoints = 0x00ff00
    private val colorCircleEventPointsActive = 0xff0000
    private val colorBeachlineIntersections = 0x00ff00
    private val colorSpikes = 0x000000
    private val colorSpikeIntersections = 0xff0000
    private val colorVoronoiSegments = 0x0000ff
    private val colorVoronoiTraces = 0xff0000
    private val colorArcs = 0x000000
    private val colorCircles = 0x000000
    private val colorDelaunay = 0x999999

    fun setPainter(painter: Painter) {
        this.painter = painter
    }

    fun paint() {

        painter.setColor(Color(colorBackground))
        painter.fillRect(0, 0, width, height)

        paintSitesAndEdges(algorithm.voronoi)

        painter.setColor(Color(colorSweepline))
        painter.drawLine(algorithm.sweepX, 0.0, algorithm.sweepX,
                height.toDouble())

        algorithm.arcs.arcs?.let { arcs ->
            paintEventQueue(algorithm.eventQueue, config.isDrawCircles)
            paintArcs(arcs, algorithm.sweepX)
        }

        algorithm.currentEvent?.let { currentEvent ->
            paintEventPoint(currentEvent,
                    config.isDrawCircles, true)
        }

        if (config.isDrawDelaunay) {
            algorithm.delaunay?.let { delaunay ->
                paintDelaunay(delaunay)
            }
        }
    }

    private fun paintDelaunay(d: Delaunay) {
        painter.setColor(Color(colorDelaunay))
        for (i in d.indices) {
            val p1 = d[i].start
            val p2 = d[i].end
            painter.drawLine(p1.x, p1.y, p2.x, p2.y)
        }
    }

    private fun paintSitesAndEdges(v: Voronoi) {
        val sites = v.sites
        val edges = v.edges

        painter.setColor(Color(colorSitesVisited))
        for (i in sites.indices) {
            val p = sites[i]
            painter.fillCircle(p.x, p.y, 3.5)
        }

        painter.setColor(Color(colorVoronoiSegments))
        if (config.isDrawVoronoiLines) {
            for (i in edges.indices) {
                val p1 = edges[i].start
                val p2 = edges[i].end
                painter.drawLine(p1.x, p1.y, p2.x, p2.y)
            }
        }
    }

    private fun paintEventQueue(queue: EventQueue, drawCircles: Boolean) {
        val iterator = queue.copy.iterator()
        while (iterator.hasNext()) {
            val eventPoint = iterator.next()
            paintEventPoint(eventPoint, drawCircles, false)
        }
    }

    private fun paintEventPoint(eventPoint: EventPoint, drawCircles: Boolean,
                                isActive: Boolean) {
        if (drawCircles || eventPoint !is CirclePoint) {
            if (eventPoint is CirclePoint) {

                painter.setColor(Color(colorCircles))
                painter.drawCircle(eventPoint.x - eventPoint.radius, eventPoint.y,
                        eventPoint.radius)

                if (isActive) {
                    painter.setColor(Color(colorCircleEventPointsActive))
                } else {
                    painter.setColor(Color(colorCircleEventPoints))
                }
                painter.fillCircle(eventPoint.x, eventPoint.y, 3.5)
            } else {
                if (isActive) {
                    painter.setColor(Color(colorSiteActive))
                } else {
                    painter.setColor(Color(colorSites))
                }
                painter.fillCircle(eventPoint.x, eventPoint.y, 3.5)
            }
        }
    }

    private fun paintArcs(arcNode: ArcNode, sweepX: Double) {
        var current: ArcNode? = arcNode
        while (current != null) {
            current.init(sweepX)
            current = current.next
        }

        ArcNodeWalker.walk(object : AbstractArcNodeVisitor() {

            override fun arc(current: ArcNode, next: ArcNode?, y1: Double,
                             y2: Double, sweepX: Double) {
                if (config.isDrawVoronoiLines) {
                    paintTraces(y2, current, sweepX)
                }

                if (config.isDrawBeach) {
                    paintBeachlineArc(y1, y2, current, sweepX)
                }
            }
        }, arcNode, height.toDouble(), sweepX)

        ArcNodeWalker.walk(object : AbstractArcNodeVisitor() {

            override fun arc(current: ArcNode, next: ArcNode?, y1: Double,
                             y2: Double, sweepX: Double) {
                if (config.isDrawBeach || config.isDrawVoronoiLines) {
                    paintBeachlineIntersections(y2, current, sweepX)
                }
            }
        }, arcNode, height.toDouble(), sweepX)

        ArcNodeWalker.walk(object : AbstractArcNodeVisitor() {

            override fun spike(current: ArcNode, next: ArcNode?, y1: Double,
                               y2: Double, sweepX: Double) {
                if (sweepX == current.x) {
                    // spikes on site events
                    if (config.isDrawBeach) {
                        paintSpike(sweepX, current, next)
                    }
                }
            }
        }, arcNode, height.toDouble(), sweepX)

    }

    private fun paintSpike(sweepX: Double, point: ArcNode, arc: ArcNode?) {
        val beachlineX = if (arc != null) sweepX - arc.f(point.y) else 0.0
        painter.setColor(Color(colorSpikes))
        painter.drawLine(beachlineX, point.y, sweepX, point.y)

        // snip debug: red dot where spike meets beachline
        painter.setColor(Color(colorSpikeIntersections))
        painter.fillCircle(beachlineX, point.y, 2.5)
        // snap debug
    }

    private fun paintBeachlineArc(yTop: Double, yBottom: Double,
                                  current: ArcNode, sweepX: Double) {
        painter.setColor(Color(colorArcs))
        // y stepping for parabola approximation
        val yStep = 3
        // yMax: clamp yBottom between 0 and 'height'
        val yMax = Math.min(Math.max(0.0, yBottom), height.toDouble())
        // initialize x1 and y1 for yTop
        var x1 = sweepX - current.f(yTop)
        var y1 = yTop
        // draw at least one segment to avoid gaps in corner cases
        var firstSegment = true

        val coords = ArrayList<Coordinate>()
        coords.add(Coordinate(x1, y1))
        // loop over y values
        var y2 = yTop + yStep
        while (y2 < yMax || firstSegment) {
            firstSegment = false
            // make last segment reach the beachline intersection
            if (y2 + yStep >= yMax) {
                y2 = yMax
            }
            val x2 = sweepX - current.f(y2)
            if (y2 > yTop && (x1 >= 0.0 || x2 >= 0.0)) {
                coords.add(Coordinate(x2, y2))
            }
            // remember coordinates values for the next round
            x1 = x2
            y2 += yStep.toDouble()
        }
        painter.drawPath(coords)
    }

    private fun paintTraces(beachY: Double, current: ArcNode, sweepX: Double) {
        val startOfTrace = current.startOfTrace
        if (startOfTrace != null) {
            val beachX = sweepX - current.f(beachY)
            painter.setColor(Color(colorVoronoiTraces))
            painter.drawLine(startOfTrace.x, startOfTrace.y, beachX,
                    beachY)
        }
    }

    private fun paintBeachlineIntersections(beachY: Double, current: ArcNode,
                                            sweepX: Double) {
        val startOfTrace = current.startOfTrace
        if (startOfTrace != null) {
            val beachX = sweepX - current.f(beachY)
            // snip debug: green dots where neighboring beachline arcs
            // intersect
            painter.setColor(Color(colorBeachlineIntersections))
            painter.fillCircle(beachX, beachY, 2.5)
            // snap debug
        }
    }

}
