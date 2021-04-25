/*
 * @(#)PointAndTangent.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.NonNull;

import java.util.function.BiFunction;

public class PointAndTangent {
    private final double x, y, tangentX, tangentY;

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

    public <T> @NonNull T getPoint(BiFunction<Double, Double, T> factory) {
        return factory.apply(x, y);
    }

    public <T> @NonNull T getTangent(BiFunction<Double, Double, T> factory) {
        return factory.apply(tangentX, tangentY);
    }

    @Override
    public String toString() {
        return "PointAndTangent{" +
                "x=" + x +
                ", y=" + y +
                ", tangentX=" + tangentX +
                ", tangentY=" + tangentY +
                '}';
    }
}
