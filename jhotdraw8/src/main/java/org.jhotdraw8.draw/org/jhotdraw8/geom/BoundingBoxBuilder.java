/*
 * @(#)BoundingBoxBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.BoundingBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Builder;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Builds a bounding box path.
 *
 * @author Werner Randelshofer
 */
public class BoundingBoxBuilder extends AbstractPathBuilder
        implements Builder<BoundingBox> {

    private double minx = Double.POSITIVE_INFINITY,
            miny = Double.POSITIVE_INFINITY,
            maxx = Double.NEGATIVE_INFINITY,
            maxy = Double.NEGATIVE_INFINITY;

    @Override
    protected void doClosePath() {
        // nothing to do
    }

    public void addToBounds(double x, double y) {
        minx = min(minx, x);
        miny = min(miny, y);
        maxx = max(maxx, x);
        maxy = max(maxy, y);
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        addToBounds(x1, y1);
        addToBounds(x2, y2);
        addToBounds(x3, y3);
    }

    @Override
    protected void doLineTo(double x, double y) {
        addToBounds(x, y);
    }

    @Override
    protected void doMoveTo(double x, double y) {
        addToBounds(x, y);
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        addToBounds(x1, y1);
        addToBounds(x2, y2);
    }

    public @Nullable Rectangle getRectangle() {
        if (Double.isNaN(minx)) {
            return null;
        }
        return new Rectangle(minx, miny, maxx - minx, maxy - miny);
    }

    public @NonNull BoundingBox build() {
        if (Double.isNaN(minx)) {
            return new BoundingBox(0, 0, 0, 0);
        }
        return new BoundingBox(minx, miny, maxx - minx, maxy - miny);
    }

    @Override
    protected void doPathDone() {
        // empty
    }
}
