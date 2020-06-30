package org.jhotdraw8.geom.offsetline;

/**
 * Axis aligned bounding box (AABB).
 */
public class AABB {
    double xMin;
    double yMin;
    double xMax;
    double yMax;

    public AABB() {

    }

    public AABB(double xMin, double yMin, double xMax, double yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }

    void expand(double val) {
        xMin -= val;
        yMin -= val;
        xMax += val;
        yMax += val;
    }
}