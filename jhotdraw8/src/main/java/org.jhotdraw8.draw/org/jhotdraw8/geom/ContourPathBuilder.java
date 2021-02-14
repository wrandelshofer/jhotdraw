/*
 * @(#)ContourPathBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.contour.ContourBuilder;
import org.jhotdraw8.geom.contour.PolyArcPath;
import org.jhotdraw8.geom.contour.PolyArcPathBuilder;

public class ContourPathBuilder extends AbstractPathBuilder {
    private final double offset;

    private final @NonNull PathBuilder consumer;
    private @NonNull PolyArcPathBuilder papb = new PolyArcPathBuilder();

    public ContourPathBuilder(@NonNull PathBuilder consumer, double offset) {
        this.offset = offset;
        this.consumer = consumer;
    }

    @Override
    protected void doClosePath() {
        papb.closePath();
    }

    @Override
    protected void doPathDone() {
        papb.pathDone();
        ContourBuilder contourBuilder = new ContourBuilder();
        for (PolyArcPath path : papb.getPaths()) {
            for (PolyArcPath contourPath : contourBuilder.parallelOffset(path, -offset)) {
                Shapes.buildFromPathIterator(consumer, contourPath.getPathIterator(null), false);
            }
        }
        consumer.pathDone();
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x, double y) {
        papb.curveTo(x1, y1, x2, y2, x, y);
    }

    @Override
    protected void doLineTo(double x, double y) {
        papb.lineTo(x, y);
    }

    @Override
    protected void doMoveTo(double x, double y) {
        papb.moveTo(x, y);
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x, double y) {
        papb.quadTo(x1, y1, x, y);
    }
}
