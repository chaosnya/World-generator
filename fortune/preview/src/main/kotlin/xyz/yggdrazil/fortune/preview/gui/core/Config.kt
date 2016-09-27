package xyz.yggdrazil.fortune.preview.gui.core

class Config {

    var isDrawCircles: Boolean = false
    var isDrawBeach: Boolean = false
    var isDrawVoronoiLines: Boolean = false
    var isDrawDelaunay: Boolean = false

    init {
        isDrawCircles = false
        isDrawBeach = true
        isDrawVoronoiLines = true
        isDrawDelaunay = false
    }
}
