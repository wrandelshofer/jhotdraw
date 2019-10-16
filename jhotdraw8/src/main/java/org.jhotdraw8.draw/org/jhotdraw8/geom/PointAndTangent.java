package org.jhotdraw8.geom;

import javafx.geometry.Point2D;

public class PointAndTangent {
    private final double x, y, tangentX, tangentY;

    public PointAndTangent(Point2D point, Point2D tangent) {
        this(point.getX(), point.getY(), tangent.getX(), tangent.getY());
    }

    public PointAndTangent(double x, double y, double tangentX, double tangentY) {
        this.x = x;
        this.y = y;
        this.tangentX = tangentX;
        this.tangentY = tangentY;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getTangentX() {
        return tangentX;
    }

    public double getTangentY() {
        return tangentY;
    }

    public Point2D getPoint() {
        return new Point2D(x, y);
    }

    public Point2D getTangent() {
        return new Point2D(tangentX, tangentY);
    }
}
