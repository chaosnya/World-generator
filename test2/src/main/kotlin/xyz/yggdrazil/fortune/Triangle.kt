package xyz.yggdrazil.fortune

import java.util.ArrayList

class Triangle(a: Site, b: Site, c: Site) {

    var sites: ArrayList<Site>? = null
        private set

    init {
        sites = ArrayList()
        sites!!.add(a)
        sites!!.add(b)
        sites!!.add(c)
    }

    fun dispose() {
        sites!!.clear()
        sites = null
    }
}