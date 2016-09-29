package xyz.yggdrazil.delaunay.voronoi.nodename.as3delaunay;

import java.util.ArrayList;

public final class Triangle {

    private ArrayList<Site> sites;

    public Triangle(Site a, Site b, Site c) {
        sites = new ArrayList();
        sites.add(a);
        sites.add(b);
        sites.add(c);
    }

    public ArrayList<Site> getSites() {
        return sites;
    }

    public void dispose() {
        sites.clear();
        sites = null;
    }
}