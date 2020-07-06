/*
 * @(#)ConvexHull.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.NonNull;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

/**
 * Provides utility methods for computing the convex hull from a set of points.
 *
 * @author Werner Randelshofer
 */
public class ConvexHull {
    /**
     * Computes the convex hull from a set of points.
     *
     * @param points the points
     * @return convex hull of the points
     */
    @NonNull
    public static List<Point2D.Double> getConvexHull2D(@NonNull List<Point2D.Double> points) {
        return Arrays.asList(getConvexHull2D(points.toArray(new Point2D.Double[0])));
    }

    /**
     * Computes the convex hull from a set of points.
     *
     * @param points the points
     * @return convex hull of the points
     */
    public static Point2D.Double[] getConvexHull2D(@NonNull Point2D.Double[] points) {
        // Quickly return if no work is needed
        if (points.length < 3) {
            return points.clone();
        }

        // Sort points from left to right O(n log n)
        Point2D.Double[] sorted = points.clone();
        Arrays.sort(sorted, (o1, o2) -> {
            int cmp = Double.compare(o1.x, o2.x);
            return (cmp == 0) ? Double.compare(o1.y, o2.y) : cmp;
        });

        Point2D.Double[] hull = new Point2D.Double[sorted.length + 2];

        // Process upper part of convex hull O(n)
        int upper = 0; // Number of points in upper part of convex hull
        hull[upper++] = sorted[0];
        hull[upper++] = sorted[1];
        for (int i = 2; i < sorted.length; i++) {
            hull[upper++] = sorted[i];
            while (upper > 2 && !isRightTurn2D(hull[upper - 3], hull[upper - 2], hull[upper - 1])) {
                hull[upper - 2] = hull[upper - 1];
                upper--;
            }
        }

        // Process lower part of convex hull O(n)
        int lower = upper; // (lower - number + 1) = number of points in the lower part of the convex hull
        hull[lower++] = sorted[sorted.length - 2];
        for (int i = sorted.length - 3; i >= 0; i--) {
            hull[lower++] = sorted[i];
            while (lower - upper > 1 && !isRightTurn2D(hull[lower - 3], hull[lower - 2], hull[lower - 1])) {
                hull[lower - 2] = hull[lower - 1];
                lower--;
            }
        }
        lower -= 1;

        // Reduce array
        Point2D.Double[] convexHull = new Point2D.Double[lower];
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
    public static boolean isRightTurn2D(@NonNull Point.Double p1, @NonNull Point.Double p2, @NonNull Point.Double p3) {
        if (p1.equals(p2) || p2.equals(p3)) {
            // no right turn if points are at same location
            return false;
        }

        double val = (p2.x * p3.y + p1.x * p2.y + p3.x * p1.y) - (p2.x * p1.y + p3.x * p2.y + p1.x * p3.y);
        return val > 0;
    }
}
