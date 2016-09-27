package xyz.yggdrazil.delaunay.voronoi.nodename.as3delaunay;

import xyz.yggdrazil.delaunay.geom.Point;
import xyz.yggdrazil.delaunay.geom.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

public final class Site implements ICoord {

    private static Stack<Site> _pool = new Stack();

    public static Site create(Point p, int index, double weight, Color color) {
        if (_pool.size() > 0) {
            return _pool.pop().init(p, index, weight, color);
        } else {
            return new Site(p, index, weight, color);
        }
    }

    public static void sortSites(ArrayList<Site> sites) {
        //sites.sort(Site.compare);
        Collections.sort(sites, new Comparator<Site>() {
            @Override
            public int compare(Site o1, Site o2) {
                return (int) Site.compare(o1, o2);
            }
        });
    }

    /**
     * sort sites on y, then x, coord also change each site's _siteIndex to
     * match its new position in the list so the _siteIndex can be used to
     * identify the site for nearest-neighbor queries
     * <p>
     * haha "also" - means more than one responsibility...
     */
    private static double compare(Site s1, Site s2) {
        int returnValue = Voronoi.compareByYThenX(s1, s2);

        // swap _siteIndex values if necessary to match new ordering:
        int tempIndex;
        if (returnValue == -1) {
            if (s1._siteIndex > s2._siteIndex) {
                tempIndex = s1._siteIndex;
                s1._siteIndex = s2._siteIndex;
                s2._siteIndex = tempIndex;
            }
        } else if (returnValue == 1) {
            if (s2._siteIndex > s1._siteIndex) {
                tempIndex = s2._siteIndex;
                s2._siteIndex = s1._siteIndex;
                s1._siteIndex = tempIndex;
            }

        }

        return returnValue;
    }

    final private static double EPSILON = .005;

    private static boolean closeEnough(Point p0, Point p1) {
        return Point.Companion.distance(p0, p1) < EPSILON;
    }

    private Point _coord;

    @Override
    public Point getCoord() {
        return _coord;
    }

    public Color color;
    public double weight;
    private int _siteIndex;
    // the edges that define this Site's Voronoi region:
    public ArrayList<Edge> _edges;
    // which end of each edge hooks up with the previous edge in edges:
    private ArrayList<LR> _edgeOrientations;
    // ordered list of points that define the region clipped to bounds:
    private ArrayList<Point> _region;

    public Site(Point p, int index, double weight, Color color) {
        init(p, index, weight, color);
    }

    private Site init(Point p, int index, double weight, Color color) {
        _coord = p;
        _siteIndex = index;
        this.weight = weight;
        this.color = color;
        _edges = new ArrayList();
        _region = null;
        return this;
    }

    @Override
    public String toString() {
        return "Site " + _siteIndex + ": " + getCoord();
    }

    private void move(Point p) {
        clear();
        _coord = p;
    }

    public void dispose() {
        _coord = null;
        clear();
        _pool.push(this);
    }

    private void clear() {
        if (_edges != null) {
            _edges.clear();
            _edges = null;
        }
        if (_edgeOrientations != null) {
            _edgeOrientations.clear();
            _edgeOrientations = null;
        }
        if (_region != null) {
            _region.clear();
            _region = null;
        }
    }

    void addEdge(Edge edge) {
        _edges.add(edge);
    }

    public Edge nearestEdge() {
        // edges.sort(Edge.compareSitesDistances);
        Collections.sort(_edges, new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                return (int) Edge.Companion.compareSitesDistances(o1, o2);
            }
        });
        return _edges.get(0);
    }

    ArrayList<Site> neighborSites() {
        if (_edges == null || _edges.isEmpty()) {
            return new ArrayList();
        }
        if (_edgeOrientations == null) {
            reorderEdges();
        }
        ArrayList<Site> list = new ArrayList();
        for (Edge edge : _edges) {
            list.add(neighborSite(edge));
        }
        return list;
    }

    private Site neighborSite(Edge edge) {
        if (this == edge.getLeftSite()) {
            return edge.getRightSite();
        }
        if (this == edge.getRightSite()) {
            return edge.getLeftSite();
        }
        return null;
    }

    ArrayList<Point> region(Rectangle clippingBounds) {
        if (_edges == null || _edges.isEmpty()) {
            return new ArrayList();
        }
        if (_edgeOrientations == null) {
            reorderEdges();
            _region = clipToBounds(clippingBounds);
            if ((new Polygon(_region)).winding() == Winding.CLOCKWISE) {
                Collections.reverse(_region);
            }
        }
        return _region;
    }

    private void reorderEdges() {
        //trace("edges:", edges);
        EdgeReorderer reorderer = new EdgeReorderer(_edges, Vertex.class);
        _edges = reorderer.getEdges();
        //trace("reordered:", edges);
        _edgeOrientations = reorderer.getEdgeOrientations();
        reorderer.dispose();
    }

    private ArrayList<Point> clipToBounds(Rectangle bounds) {
        ArrayList<Point> points = new ArrayList();
        int n = _edges.size();
        int i = 0;
        Edge edge;
        while (i < n && (!_edges.get(i).getVisible())) {
            ++i;
        }

        if (i == n) {
            // no edges visible
            return new ArrayList();
        }
        edge = _edges.get(i);
        LR orientation = _edgeOrientations.get(i);
        points.add(edge.getClippedEnds().get(orientation));
        points.add(edge.getClippedEnds().get((LR.other(orientation))));

        for (int j = i + 1; j < n; ++j) {
            edge = _edges.get(j);
            if (!edge.getVisible()) {
                continue;
            }
            connect(points, j, bounds, false);
        }
        // close up the polygon by adding another corner point of the bounds if needed:
        connect(points, i, bounds, true);

        return points;
    }

    private void connect(ArrayList<Point> points, int j, Rectangle bounds, boolean closingUp) {
        Point rightPoint = points.get(points.size() - 1);
        Edge newEdge = _edges.get(j);
        LR newOrientation = _edgeOrientations.get(j);
        // the point that  must be connected to rightPoint:
        Point newPoint = newEdge.getClippedEnds().get(newOrientation);
        if (!closeEnough(rightPoint, newPoint)) {
            // The points do not coincide, so they must have been clipped at the bounds;
            // see if they are on the same border of the bounds:
            if (rightPoint.getX() != newPoint.getX()
                    && rightPoint.getY() != newPoint.getY()) {
                // They are on different borders of the bounds;
                // insert one or two corners of bounds as needed to hook them up:
                // (NOTE this will not be correct if the region should take up more than
                // half of the bounds rect, for then we will have gone the wrong way
                // around the bounds and included the smaller part rather than the larger)
                int rightCheck = BoundsCheck.check(rightPoint, bounds);
                int newCheck = BoundsCheck.check(newPoint, bounds);
                double px, py;
                if ((rightCheck & BoundsCheck.RIGHT) != 0) {
                    px = bounds.getRight();
                    if ((newCheck & BoundsCheck.BOTTOM) != 0) {
                        py = bounds.getBottom();
                        points.add(new Point(px, py));
                    } else if ((newCheck & BoundsCheck.TOP) != 0) {
                        py = bounds.getTop();
                        points.add(new Point(px, py));
                    } else if ((newCheck & BoundsCheck.LEFT) != 0) {
                        if (rightPoint.getY() - bounds.getY() + newPoint.getY() - bounds.getY() < bounds.getHeight()) {
                            py = bounds.getTop();
                        } else {
                            py = bounds.getBottom();
                        }
                        points.add(new Point(px, py));
                        points.add(new Point(bounds.getLeft(), py));
                    }
                } else if ((rightCheck & BoundsCheck.LEFT) != 0) {
                    px = bounds.getLeft();
                    if ((newCheck & BoundsCheck.BOTTOM) != 0) {
                        py = bounds.getBottom();
                        points.add(new Point(px, py));
                    } else if ((newCheck & BoundsCheck.TOP) != 0) {
                        py = bounds.getTop();
                        points.add(new Point(px, py));
                    } else if ((newCheck & BoundsCheck.RIGHT) != 0) {
                        if (rightPoint.getY() - bounds.getY() + newPoint.getY() - bounds.getY() < bounds.getHeight()) {
                            py = bounds.getTop();
                        } else {
                            py = bounds.getBottom();
                        }
                        points.add(new Point(px, py));
                        points.add(new Point(bounds.getRight(), py));
                    }
                } else if ((rightCheck & BoundsCheck.TOP) != 0) {
                    py = bounds.getTop();
                    if ((newCheck & BoundsCheck.RIGHT) != 0) {
                        px = bounds.getRight();
                        points.add(new Point(px, py));
                    } else if ((newCheck & BoundsCheck.LEFT) != 0) {
                        px = bounds.getLeft();
                        points.add(new Point(px, py));
                    } else if ((newCheck & BoundsCheck.BOTTOM) != 0) {
                        if (rightPoint.getX() - bounds.getX() + newPoint.getX() - bounds.getX() < bounds.getWidth()) {
                            px = bounds.getLeft();
                        } else {
                            px = bounds.getRight();
                        }
                        points.add(new Point(px, py));
                        points.add(new Point(px, bounds.getBottom()));
                    }
                } else if ((rightCheck & BoundsCheck.BOTTOM) != 0) {
                    py = bounds.getBottom();
                    if ((newCheck & BoundsCheck.RIGHT) != 0) {
                        px = bounds.getRight();
                        points.add(new Point(px, py));
                    } else if ((newCheck & BoundsCheck.LEFT) != 0) {
                        px = bounds.getLeft();
                        points.add(new Point(px, py));
                    } else if ((newCheck & BoundsCheck.TOP) != 0) {
                        if (rightPoint.getX() - bounds.getX() + newPoint.getX() - bounds.getX() < bounds.getWidth()) {
                            px = bounds.getLeft();
                        } else {
                            px = bounds.getRight();
                        }
                        points.add(new Point(px, py));
                        points.add(new Point(px, bounds.getTop()));
                    }
                }
            }
            if (closingUp) {
                // newEdge's ends have already been added
                return;
            }
            points.add(newPoint);
        }
        Point newRightPoint = newEdge.getClippedEnds().get(LR.other(newOrientation));
        if (!closeEnough(points.get(0), newRightPoint)) {
            points.add(newRightPoint);
        }
    }

    public double get_x() {
        return _coord.getX();
    }

    public double get_y() {
        return _coord.getY();
    }

    public double dist(ICoord p) {
        return Point.Companion.distance(p.getCoord(), this._coord);
    }
}

final class BoundsCheck {

    final public static int TOP = 1;
    final public static int BOTTOM = 2;
    final public static int LEFT = 4;
    final public static int RIGHT = 8;

    /**
     * @param point
     * @param bounds
     * @return an int with the appropriate bits set if the Point lies on the
     * corresponding bounds lines
     */
    public static int check(Point point, Rectangle bounds) {
        int value = 0;
        if (point.getX() == bounds.getLeft()) {
            value |= LEFT;
        }
        if (point.getX() == bounds.getRight()) {
            value |= RIGHT;
        }
        if (point.getY() == bounds.getTop()) {
            value |= TOP;
        }
        if (point.getY() == bounds.getBottom()) {
            value |= BOTTOM;
        }
        return value;
    }

}
