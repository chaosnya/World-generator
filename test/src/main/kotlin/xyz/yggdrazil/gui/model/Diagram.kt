package xyz.yggdrazil.gui.model

import xyz.yggdrazil.math.geometry.Polygon
import java.util.*

/**
 * Created by Alexandre Mommers on 23/09/16.
 */
abstract class Diagram<P : Polygon> {


    val polygons = LinkedList<P>()

    /**
     * should generate diagram with current configuration
     */
    abstract fun compute(): List<P>
}