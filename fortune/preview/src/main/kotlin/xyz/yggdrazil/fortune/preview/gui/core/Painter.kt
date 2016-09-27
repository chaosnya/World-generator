package xyz.yggdrazil.fortune.preview.gui.core

interface Painter {
    fun setColor(color: Color)

    fun fillRect(x: Int, y: Int, width: Int, height: Int)

    fun fillRect(x: Double, y: Double, width: Double, height: Double)

    fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int)

    fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double)

    fun drawPath(points: List<Coordinate>)

    fun fillPath(points: List<Coordinate>)

    fun drawCircle(x: Double, y: Double, radius: Double)

    fun fillCircle(x: Double, y: Double, radius: Double)

}
