package xyz.yggdrazil.delaunay.voronoi.nodename.as3delaunay;

public final class Winding {

    final public static Winding CLOCKWISE = new Winding("clockwise");
    final public static Winding COUNTERCLOCKWISE = new Winding("counterclockwise");
    final public static Winding NONE = new Winding("none");
    private String name;

    private Winding(String name) {
        super();
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}