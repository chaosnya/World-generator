package xyz.yggdrazil.midgard.map.biome

/**
 * Created by Alexandre Mommers on 01/11/2016.
 *
 * abstract class to represent a set of biomes
 */
abstract class BiomeType {

    /**
     * this class represent a biome at a specific place
     */
    open class ZoneBiome {

    }

    /**
     * return a specific which mach given condition
     */
    abstract fun getBiome(height: Int, heatFactor: Double = .0, rainFactor: Double = .0): ZoneBiome
}
