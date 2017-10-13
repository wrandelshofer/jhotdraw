/* @(#)TransformPathBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;
import javafx.scene.transform.Transform;

/**
 * TransformPathBuilder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TransformPathBuilder extends AbstractPathBuilder {

    private final PathBuilder target;
    private Transform transform;

    public TransformPathBuilder(PathBuilder target) {
        this.target = target;
    }

    @Override
    protected void doClosePath() {
        target.closePath();
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        if (transform == null) {
            target.curveTo(x1, y1, x2, y2, x3, y3);
        } else {
            Point2D p1 = transform.transform(x1, y1);
            Point2D p2 = transform.transform(x2, y2);
            Point2D p3 = transform.transform(x3, y3);
            target.curveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
        }
    }

    @Override
    protected void doFinish() {
       target.pathDone();
    }

    @Override
    protected void doLineTo(double x, double y) {
        if (transform == null) {
            target.lineTo(x,y);
        } else {
            Point2D p = transform.transform(x, y);
            target.lineTo(p.getX(), p.getY());
        }
    }

    @Override
    protected void doMoveTo(double x, double y) {
        if (transform == null) {
            target.moveTo(x,y);
        } else {
            Point2D p = transform.transform(x, y);
            target.moveTo(p.getX(), p.getY());
        }
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        if (transform == null) {
            target.quadTo(x1, y1, x2, y2);
        } else {
            Point2D p1 = transform.transform(x1, y1);
            Point2D p2 = transform.transform(x2, y2);
            target.quadTo(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

}
