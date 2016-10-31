package xyz.yggdrazil.midgard.demo.fortune

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import xyz.yggdrazil.fortune.Voronoi

/**
 * Created by amo on 28/10/16.
 */
class FortuneRender {

    data class Settings(var showVoronoi: Boolean = true,
                        var showDelaunay: Boolean = true)
    val settings = Settings()

    fun draw(gc: GraphicsContext, voronoi: Voronoi) {
        gc.setFill(Color.GREEN)
        gc.setStroke(Color.BLUE)
        gc.setLineWidth(5.0)
        gc.strokeLine(40.0, 10.0, 10.0, 40.0)
        gc.fillOval(10.0, 60.0, 30.0, 30.0)
        gc.strokeOval(60.0, 60.0, 30.0, 30.0)
        gc.fillRoundRect(110.0, 60.0, 30.0, 30.0, 10.0, 10.0)
        gc.strokeRoundRect(160.0, 60.0, 30.0, 30.0, 10.0, 10.0)
        gc.fillArc(10.0, 110.0, 30.0, 30.0, 45.0, 240.0, ArcType.OPEN)
        gc.fillArc(60.0, 110.0, 30.0, 30.0, 45.0, 240.0, ArcType.CHORD)
        gc.fillArc(110.0, 110.0, 30.0, 30.0, 45.0, 240.0, ArcType.ROUND)
        gc.strokeArc(10.0, 160.0, 30.0, 30.0, 45.0, 240.0, ArcType.OPEN)
        gc.strokeArc(60.0, 160.0, 30.0, 30.0, 45.0, 240.0, ArcType.CHORD)
        gc.strokeArc(110.0, 160.0, 30.0, 30.0, 45.0, 240.0, ArcType.ROUND)
        gc.fillPolygon(doubleArrayOf(10.0, 40.0, 10.0, 40.0),
                doubleArrayOf(210.0, 210.0, 240.0, 240.0), 4)
        gc.strokePolygon(doubleArrayOf(60.0, 90.0, 60.0, 90.0),
                doubleArrayOf(210.0, 210.0, 240.0, 240.0), 4)
        gc.strokePolyline(doubleArrayOf(110.0, 140.0, 110.0, 140.0),
                doubleArrayOf(210.0, 210.0, 240.0, 240.0), 4)

    }
}