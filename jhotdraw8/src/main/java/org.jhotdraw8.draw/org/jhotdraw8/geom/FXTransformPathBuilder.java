/*
 * @(#)FXTransformPathBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;

/**
 * TransformPathBuilder.
 *
 * @author Werner Randelshofer
 */
public class FXTransformPathBuilder extends AbstractPathBuilder {

    private final @NonNull PathBuilder target;
    private @NonNull Transform transform;

    public FXTransformPathBuilder(@NonNull PathBuilder target) {
        this(target, FXTransforms.IDENTITY);
    }

    public FXTransformPathBuilder(@NonNull PathBuilder target, @NonNull Transform transform) {
        this.target = target;
        this.transform = transform;
    }

    @Override
    protected void doClosePath() {
        target.closePath();
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        Point2D p1 = transform.transform(x1, y1);
        Point2D p2 = transform.transform(x2, y2);
        Point2D p3 = transform.transform(x3, y3);
        target.curveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
    }

    @Override
    protected void doPathDone() {
        target.pathDone();
    }

    @Override
    protected void doLineTo(double x, double y) {
        Point2D p = transform.transform(x, y);
        target.lineTo(p.getX(), p.getY());
    }

    @Override
    protected void doMoveTo(double x, double y) {
        Point2D p = transform.transform(x, y);
        target.moveTo(p.getX(), p.getY());
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        Point2D p1 = transform.transform(x1, y1);
        Point2D p2 = transform.transform(x2, y2);
        target.quadTo(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public @NonNull Transform getTransform() {
        return transform;
    }

    public void setTransform(@NonNull Transform transform) {
        this.transform = transform;
    }

}
