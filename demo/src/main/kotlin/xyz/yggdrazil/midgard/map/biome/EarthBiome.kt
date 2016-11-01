package xyz.yggdrazil.midgard.map.biome

/**
 * Created by Alexandre Mommers on 01/11/2016.
 */
class EarthBiome : Biome() {

    val maxElevation = 10000
    val maxCelsiusTemperature = 10000

    enum class EarthZone(val zone: Zone) {
        SandDesert(Zone()),
        MountainDesert(Zone())

    }

    override fun getZone(height: Int, heatFactor: Double, rainFactor: Double): Zone {
        return getEarthZone(height, heatFactor, rainFactor).zone
    }

    fun getEarthZone(height: Int, heatFactor: Double, rainFactor: Double): EarthZone {
        return when (height) {
            in 0..1000 -> EarthZone.SandDesert
            else -> EarthZone.MountainDesert
        }
    }
}
