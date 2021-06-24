/*
 * @(#)EmptySegmentsSkippingPathBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.NonNull;

/**
 * Skips lineTo, quadTo and curveTo segments
 * if the distance to the previous segment is less than epsilon.
 */
public class EmptySkippingPathBuilder extends AbstractPathBuilder {

    private final @NonNull PathBuilder consumer;


    /**
     * Squared Epsilon for determining whether an element is empty.
     */
    private final double squaredEpsilon;

    public EmptySkippingPathBuilder(@NonNull PathBuilder consumer, double epsilon) {
        this.consumer = consumer;
        this.squaredEpsilon = epsilon * epsilon;
    }


    protected void doArcTo(double radiusX, double radiusY, double xAxisRotation, double x, double y, boolean largeArcFlag, boolean sweepFlag) {
        if (shouldntSkip(x, y)) {
            consumer.arcTo(radiusX, radiusY, xAxisRotation, x, y, largeArcFlag, sweepFlag);
        }
    }

    protected void doClosePath() {
        consumer.closePath();
    }

    protected void doPathDone() {
        consumer.pathDone();

    }

    protected void doCurveTo(double x1, double y1, double x2, double y2, double x, double y) {
        if (shouldntSkip(x, y)) {
            consumer.curveTo(x1, y1, x2, y2, x, y);
        }
    }

    protected void doLineTo(double x, double y) {
        if (shouldntSkip(x, y)) {
            consumer.lineTo(x, y);
        }
    }

    protected void doMoveTo(double x, double y) {
        consumer.moveTo(x, y);
    }

    protected void doQuadTo(double x1, double y1, double x, double y) {
        if (shouldntSkip(x, y)) {
            consumer.quadTo(x1, y1, x, y);
        }
    }


    private boolean shouldntSkip(double x, double y) {
        return Geom.squaredDistance(getLastX(), getLastY(), x, y) >= squaredEpsilon;
    }
}
