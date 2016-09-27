package xyz.yggdrazil.fortune.arc

object ArcNodeWalker {
    fun walk(visitor: ArcNodeVisitor, arcNode: ArcNode?,
             height: Double, sweepX: Double) {
        var y1 = 0.0
        var y2 = height

        var current: ArcNode? = arcNode
        while (current != null) {
            val next = current.next

            if (sweepX == current.x) {
                // spikes on site events
                visitor.spike(current, next, y1, y2, sweepX)
                y2 = current.y
            } else {
                if (next == null) {
                    y2 = height
                } else {
                    if (sweepX == next.x) {
                        y2 = next.y
                    } else {
                        try {
                            val ad = ParabolaPoint.solveQuadratic(
                                    current.a - next.a,
                                    current.b - next.b,
                                    current.c - next.c)
                            y2 = ad[0]
                        } catch (e: MathException) {
                            y2 = y1
                            println("*** error: No parabola intersection while painting arc - SLine: "
                                    + sweepX
                                    + ", "
                                    + current.toString()
                                    + " "
                                    + next.toString())
                        }

                    }
                }
                // beachline arcs
                visitor.arc(current, next, y1, y2, sweepX)
            }

            y1 = Math.max(0.0, y2)
            current = current.next
        }
    }
}
