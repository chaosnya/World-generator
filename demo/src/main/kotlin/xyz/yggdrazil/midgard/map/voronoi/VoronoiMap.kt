package xyz.yggdrazil.midgard.map.voronoi

import xyz.yggdrazil.midgard.map.graph.MapGraph
import xyz.yggdrazil.midgard.map.graph.MapNode
import xyz.yggdrazil.midgard.math.fortune.Voronoi
import xyz.yggdrazil.midgard.math.geometry.Point
import xyz.yggdrazil.midgard.math.lloydRelaxation.relax
import java.util.*

/**
 * Created by Alexandre Mommers on 02/11/2016.
 */
class VoronoiMap(settings: VoronoiMapSettings) : MapGraph<MapNode, VoronoiMapSettings>(settings) {

    override fun generate(completion: (Double) -> Unit) {

        /* generate nodes */
        val geometry = buildInitialGeometry()
        applyGeometryToMap(geometry)

    }

    private fun buildInitialGeometry(): Voronoi {
        val random = Random(settings.seed)
        var voronoi = Voronoi.generate(
                settings.sites,
                1000.toDouble(), 1000.toDouble(),
                random
        )

        for (i in 1..settings.lloydRelaxations) {
            voronoi = voronoi.relax()
        }

        return voronoi
    }

    private fun applyGeometryToMap(voronoi: Voronoi) {
        val nodeByCoords = HashMap<Point, MapNode>()

        //create node
        voronoi.siteCoords()
                .forEach { coordinate ->
                    val points = voronoi.region(coordinate)
                    val node = MapNode()
                    node.addAll(points)
                    add(node)

                    nodeByCoords[coordinate] = node
                }

        //link nodes
        voronoi.siteCoords()
                .forEach { coordinate ->
                    val node = nodeByCoords[coordinate]

                    voronoi.neighborSitesForSite(coordinate)
                        .forEach { neighborCoordinate ->
                            nodeByCoords[neighborCoordinate]?.let { neighbor ->
                                node?.neighbours?.add(neighbor)
                            }
                        }
                }
    }
}