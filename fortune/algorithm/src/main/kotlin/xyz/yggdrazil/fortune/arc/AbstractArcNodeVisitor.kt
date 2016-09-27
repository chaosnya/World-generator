package xyz.yggdrazil.fortune.arc

abstract class AbstractArcNodeVisitor : ArcNodeVisitor {

    override fun spike(current: ArcNode, next: ArcNode?, y1: Double, y2: Double,
                       sweepX: Double) {
        // ignore
    }

    override fun arc(current: ArcNode, next: ArcNode?, y1: Double, y2: Double,
                     sweepX: Double) {
        // ignore
    }

}
