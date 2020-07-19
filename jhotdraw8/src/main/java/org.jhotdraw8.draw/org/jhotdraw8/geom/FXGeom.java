package org.jhotdraw8.geom;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;

/**
 * This class provides utility functions for two dimensional geometry
 * based on the javafx.graphics module (FX), as well as translation
 * functions from/to geometric objects of the java.desktop module (AWT).
 * .
 */
public class FXGeom {
    private FXGeom() {};

    /**
     * Gets the bounds of the specified shape.
     *
     * @param shape an AWT shape
     * @return JavaFX bounds
     */
    @NonNull
    public static BoundingBox getBounds(@NonNull java.awt.Shape shape) {
        java.awt.geom.Rectangle2D r = shape.getBounds2D();
        return new BoundingBox(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * Computes the linear interpolation/extrapolation between two points.
     *
     * @param start point a
     * @param end   point b
     * @param t     a value between [0, 1] defines the interpolation between a and
     *              b. Values outside this range yield an extrapolation.
     * @return the interpolated or extrapolated value
     */
    @NonNull
    public static Point2D lerp(@NonNull Point2D start, @NonNull Point2D end, double t) {
        return lerp(start.getX(), start.getY(), end.getX(), end.getY(), t);
    }

    @NonNull
    public static Point2D lerp(double x0, double y0, double x1, double y1, double t) {
        return new Point2D(x0 + (x1 - x0) * t, y0 + (y1 - y0) * t);
    }
}
