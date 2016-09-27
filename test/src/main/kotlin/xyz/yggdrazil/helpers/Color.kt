package xyz.yggdrazil.helpers

import java.util.*

/**
 * Created by Alexandre Mommers on 05/09/16.
 */

class Color(color: Int) : java.awt.Color(color) {


    companion object {

        fun random(): Color {
            val random = Random()
            return Color(java.awt.Color.HSBtoRGB(random.nextFloat(), 1.0f, 1.0f))
        }

        fun random(random: Random): Color {
            return Color(java.awt.Color.HSBtoRGB(random.nextFloat(), 1.0f, 1.0f))
        }
    }

}
