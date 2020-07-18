package org.jhotdraw8.geom.offsetline;

/**
 * Axis aligned bounding box (AABB).
 */
public class AABB {
    final double xMin;
    final double yMin;
    final double xMax;
    final double yMax;

    public AABB(double xMin, double yMin, double xMax, double yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }
}