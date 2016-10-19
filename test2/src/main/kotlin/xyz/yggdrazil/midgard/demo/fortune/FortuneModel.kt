package xyz.yggdrazil.midgard.demo.fortune

/**
 * Created by Alexandre Mommers on 18/10/16.
 */
class FortuneModel {
    data class Settings(var sites: Int = 10000,
                        var loydRelxations: Int = 2,
                        var seed: Long = System.nanoTime())

    val settings = Settings()
}