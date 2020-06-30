package org.jhotdraw8.geom.offsetline;


import javafx.geometry.Point2D;

/**
 * Vertex of a Polyline Arc.
 */
public class PlineVertex {
    private double x;
    private double y;
    private double bulge;

    public PlineVertex(Point2D p, double bulge) {
        this(p.getX(), p.getY(), bulge);
    }

    public PlineVertex(double x, double y) {
        this(x, y, 0.0);
    }

    public PlineVertex(double x, double y, double bulge) {
        this.x = x;
        this.y = y;
        this.bulge = bulge;
    }

    public boolean bulgeIsNeg() {
        return bulge < 0;
    }

    public boolean bulgeIsZero() {
        return bulgeIsZero(OffsetPathBuilder.realPrecision);
    }

    public boolean bulgeIsZero(double epsilon) {
        return Math.abs(bulge) < epsilon;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double bulge() {
        return bulge;
    }

    public void bulge(double bulge) {
        this.bulge = bulge;
    }

    public Point2D pos() {
        return new Point2D(x, y);
    }

    public void pos(Point2D p) {
        x = p.getX();
        y = p.getY();
    }
}
