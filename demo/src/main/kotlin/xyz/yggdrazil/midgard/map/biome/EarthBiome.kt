package xyz.yggdrazil.midgard.map.biome

/**
 * Created by Alexandre Mommers on 01/11/2016.
 *
 * Simulate a biome set that we can find on earth like that
 * https://fr.wikipedia.org/wiki/Biome#/media/File:Holdridge_FR.png
 */
class EarthBiome : BiomeType() {

    /**
     * aproximatively the higer mountain on earth
     */
    val maxElevation = 10000

    /**
     * not really the max temperature that we can found on earth but that a simulation right ?
     */
    val maxCelsiusTemperature = 60

    enum class EarthZone(val zone: ZoneBiome) {
        SandDesert(ZoneBiome()),
        MountainDesert(ZoneBiome())

    }

    override fun getBiome(height: Int, heatFactor: Double, rainFactor: Double): ZoneBiome {
        return getEarthZone(height, heatFactor, rainFactor).zone
    }

    fun getEarthZone(height: Int, heatFactor: Double, rainFactor: Double): EarthZone {
        return when (height) {
            in 0..1000 -> EarthZone.SandDesert
            else -> EarthZone.MountainDesert
        }
    }
}
