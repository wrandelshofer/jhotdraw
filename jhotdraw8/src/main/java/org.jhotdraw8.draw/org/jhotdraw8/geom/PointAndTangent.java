package org.jhotdraw8.geom;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;

public class PointAndTangent {
    private final double x, y, tangentX, tangentY;

    public PointAndTangent(@NonNull Point2D point, @NonNull Point2D tangent) {
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

    @NonNull
    public Point2D getPoint() {
        return new Point2D(x, y);
    }

    @NonNull
    public Point2D getTangent() {
        return new Point2D(tangentX, tangentY);
    }
}
