package xyz.yggdrazil.midgard.map.graph

import java.util.*

/**
 * Created by Alexandre Mommers on 03/11/2016.
 */
abstract class MapGraph<N : MapNode, S : MapSettings>(
        val settings: S,
        completion: (Double) -> Unit = {}
) : HashSet<N>() {

    init {
        generate(completion)
    }

    /**
     * generate a map with given parameters
     */
    abstract fun generate(completion: (Double) -> Unit)
}