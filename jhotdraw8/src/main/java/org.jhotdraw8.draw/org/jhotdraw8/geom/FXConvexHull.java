/*
 * @(#)FXConvexHull.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Provides utility methods for computing the convex hull from a set of points.
 *
 * @author Werner Randelshofer
 */
public class FXConvexHull {

    /**
     * Computes the convex hull from a set of points.
     *
     * @param points the points
     * @return convex hull of the points
     */
    @NonNull
    public static List<Point2D> getConvexHull(@NonNull List<Point2D> points) {
        return Arrays.asList(getConvexHull(points.toArray(new Point2D[0])));
    }

    /**
     * Computes the convex hull from a set of points.
     *
     * @param points the points
     * @return convex hull of the points
     */
    public static Point2D[] getConvexHull(@NonNull Point2D[] points) {
        // Quickly return if no work is needed
        if (points.length < 3) {
            return points.clone();
        }

        // Sort points from left to right O(n log n)
        Point2D[] sorted = points.clone();
        Arrays.sort(sorted, new Comparator<Point2D>() {

            @Override
            public int compare(@NonNull Point2D o1, @NonNull Point2D o2) {
                double v = o1.getX() - o2.getX();
                if (v == 0) {
                    v = o1.getY() - o2.getY();
                }
                return (v > 0) ? 1 : ((v < 0) ? -1 : 0);
            }
        });

        Point2D[] hull = new Point2D[sorted.length + 2];

        // Process upper part of convex hull O(n)
        int upper = 0; // Number of points in upper part of convex hull
        hull[upper++] = sorted[0];
        hull[upper++] = sorted[1];
        for (int i = 2; i < sorted.length; i++) {
            hull[upper++] = sorted[i];
            while (upper > 2 && !isRightTurn(hull[upper - 3], hull[upper - 2], hull[upper - 1])) {
                hull[upper - 2] = hull[upper - 1];
                upper--;
            }
        }

        // Process lower part of convex hull O(n)
        int lower = upper; // (lower - number + 1) = number of points in the lower part of the convex hull
        hull[lower++] = sorted[sorted.length - 2];
        for (int i = sorted.length - 3; i >= 0; i--) {
            hull[lower++] = sorted[i];
            while (lower - upper > 1 && !isRightTurn(hull[lower - 3], hull[lower - 2], hull[lower - 1])) {
                hull[lower - 2] = hull[lower - 1];
                lower--;
            }
        }
        lower -= 1;

        // Reduce array
        Point2D[] convexHull = new Point2D[lower];
        System.arraycopy(hull, 0, convexHull, 0, lower);
        return convexHull;
    }

    /**
     * Returns true, if the three given points make a right turn.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @return true if right turn.
     */
    public static boolean isRightTurn(@NonNull Point2D p1, @NonNull Point2D p2, @NonNull Point2D p3) {
        if (p1.equals(p2) || p2.equals(p3)) {
            // no right turn if points are at same location
            return false;
        }

        double val = (p2.getX() * p3.getY() + p1.getX() * p2.getY() + p3.getX() * p1.getY()) - (p2.getX() * p1.getY() + p3.getX() * p2.getY() + p1.getX() * p3.getY());
        return val > 0;
    }
}
