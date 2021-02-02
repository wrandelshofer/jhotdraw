/*
 * @(#)AABB.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

/**
 * Axis aligned bounding box (AABB).
 */
public class AABB {
    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;

    public AABB(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getWidth() {
        return maxX-minX;
    }

    public double getHeight() {
        return maxY-minY;
    }


}