package xyz.yggdrazil.delaunay.voronoi.nodename.as3delaunay;

import xyz.yggdrazil.delaunay.geom.Point;

import java.util.Stack;

final public class Vertex implements ICoord {

    final public static Vertex VERTEX_AT_INFINITY = new Vertex(Double.NaN, Double.NaN);
    final private static Stack<Vertex> _pool = new Stack();
    private static int nvertices = 0;
    private Point coord;
    private int vertexIndex;

    public Vertex(double x, double y) {
        init(x, y);
    }

    private static Vertex create(double x, double y) {

        if (Double.isNaN(x) || Double.isNaN(y)) {
            return VERTEX_AT_INFINITY;
        }
        if (_pool.size() > 0) {

            return _pool.pop().init(x, y);
        } else {
            return new Vertex(x, y);
        }
    }

    /**
     * This is the only way to make a Vertex
     *
     * @param halfedge0
     * @param halfedge1
     * @return
     */
    public static Vertex intersect(Halfedge halfedge0, Halfedge halfedge1) {
        Edge edge0, edge1, edge;
        Halfedge halfedge;
        double determinant, intersectionX, intersectionY;
        boolean rightOfSite;

        edge0 = halfedge0.getEdge();
        edge1 = halfedge1.getEdge();
        if (edge0 == null || edge1 == null) {
            return null;
        }
        if (edge0.getRightSite() == edge1.getRightSite()) {
            return null;
        }

        determinant = edge0.getA() * edge1.getB() - edge0.getB() * edge1.getA();
        if (-1.0e-10 < determinant && determinant < 1.0e-10) {
            // the edges are parallel
            return null;
        }

        intersectionX = (edge0.getC() * edge1.getB() - edge1.getC() * edge0.getB()) / determinant;
        intersectionY = (edge1.getC() * edge0.getA() - edge0.getC() * edge1.getA()) / determinant;

        if (Voronoi.compareByYThenX(edge0.getRightSite(), edge1.getRightSite()) < 0) {
            halfedge = halfedge0;
            edge = edge0;
        } else {
            halfedge = halfedge1;
            edge = edge1;
        }
        rightOfSite = intersectionX >= edge.getRightSite().get_x();
        if ((rightOfSite && halfedge.getLeftRight() == LR.Companion.getLEFT())
                || (!rightOfSite && halfedge.getLeftRight() == LR.Companion.getRIGHT())) {
            return null;
        }

        return Vertex.create(intersectionX, intersectionY);
    }

    @Override
    public Point getCoord() {
        return coord;
    }

    public int getVertexIndex() {
        return vertexIndex;
    }

    private Vertex init(double x, double y) {
        coord = new Point(x, y);
        return this;
    }

    public void dispose() {
        coord = null;
        _pool.push(this);
    }

    public void setIndex() {
        vertexIndex = nvertices++;
    }

    @Override
    public String toString() {
        return "Vertex (" + vertexIndex + ")";
    }

    public double get_x() {
        return coord.getX();
    }

    public double get_y() {
        return coord.getY();
    }
}
