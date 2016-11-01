package xyz.yggdrazil.midgard.map.biome

/**
 * Created by Alexandre Mommers on 01/11/2016.
 */
abstract class Biome {

    open class Zone {

    }

    abstract fun getZone(height: Int, heatFactor: Double, rainFactor: Double): Zone
}
