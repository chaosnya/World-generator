package xyz.yggdrazil.fortune.arc

interface ArcNodeVisitor {

    fun spike(current: ArcNode, next: ArcNode?, y1: Double, y2: Double,
              sweepX: Double)

    fun arc(current: ArcNode, next: ArcNode?, y1: Double, y2: Double,
            sweepX: Double)

}
