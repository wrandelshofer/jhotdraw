/* @(#)NineRegionsScalingBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * This builder slices the input path into 9 regions, and scales them by the
 * specified scale factor and pivot.
 * <p>
 * The builder takes a four values minx, miny, maxx, maxy as input, or a
 * rectangle and insets from which these four values are computed.
 * <p>
 * The coordinate space is split along minx, miny, maxx, maxy into 9 pieces:
 * top-left, top-right, bottom-right, bottom-left, top, right, bottom, left,
 * center.
 * <ul>
 * <li>the top left piece is translated by the scaled distance from the left and
 * the top inset to the pivot.</li>
 * <li>similar translations are applied to the top right, bottom right and
 * bottom left pieces.</li>
 * <li>the top piece is translated by the scaled distance from the top inset to
 * the pivot,and scaled by the sx-factor of the scaling.</li>
 * <li>similar transformations are applied to the right, bottom and left
 * pieces.</li>
 * <li>the piece in the center is scaled by the scale factor around the
 * pivot</li>
 * </ul>
 *
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NineRegionsScalingBuilder extends AbstractPathBuilder {

    private final double minX, minY, maxX, maxY;
    private final Transform topLeft, topRight, bottomRight, bottomLeft, top, right, bottom, left, center;
    private final PathBuilder target;

    /**
     * Creates a new instance.
     *
     * @param dest The destination (target) of the builder.
     * @param srcBounds The bounds of the source image.
     * @param srcInsets The nine regions in the bounds of the source image.
     * @param destBounds The bounds of the destination image.
     */
    public NineRegionsScalingBuilder(PathBuilder dest, Bounds srcBounds, Insets srcInsets, Bounds destBounds) {
        this.target = dest;

        double it = srcInsets.getTop(), ib = srcInsets.getBottom(), ir = srcInsets.getRight(), il = srcInsets.getLeft();

        Bounds si = Geom.subtractInsets(srcBounds, srcInsets);
        this.minX = si.getMinX();
        this.maxX = si.getMaxX();
        this.minY = si.getMinY();
        this.maxY = si.getMaxY();

        double sx = srcBounds.getMinX(),
                sy = srcBounds.getMinY(),
                sw = srcBounds.getWidth(),
                sh = srcBounds.getHeight();
        double dx = destBounds.getMinX(),
                dy = destBounds.getMinY(),
                dw = destBounds.getWidth(),
                dh = destBounds.getHeight();

        center = Transforms.createReshapeTransform(
                sx + il, sy + it, sw - il - ir, sh - it - ib,
                dx + il, dy + it, dw - il - ir, dh - it - ib
        );
        top = Transforms.createReshapeTransform(
                sx + il, sy + it, sw - il - ir, it,
                dx + il, dy + it, dw - il - ir, it
        );
        bottom = Transforms.createReshapeTransform(
                sx + il, sy + sh - ib, sw - il - ir, ib,
                dx + il, dy + dh - ib, dw - il - ir, ib
        );
        left = Transforms.createReshapeTransform(
                sx, sy + it, il, sh - it - ib,
                dx, dy + it, il, dh - it - ib
        );
        right = Transforms.createReshapeTransform(
                sx + sw - ir, sy + it, ir, sh - it - ib,
                dx + dw - ir, dy + it, ir, dh - it - ib
        );
        topLeft = Transforms.createReshapeTransform(
                sx, sy, il, it,
                dx, dy, il, it
        );
        bottomLeft = Transforms.createReshapeTransform(
                sx, sy + sh - ib, il, ib,
                dx, dy + sh - ib, il, ib
        );
        topRight = Transforms.createReshapeTransform(
                sx + sw - ir, sy, ir, it,
                dx + dw - ir, dy, ir, it
        );
        bottomRight = Transforms.createReshapeTransform(
                sx + sw - ir, sy + sh - ib, ir, ib,
                dx + sw - ir, dy + dh - ib, ir, ib
        );
    }

    @Override
    protected void doClosePath() {
        target.closePath();
    }

    private Point2D transform(double x, double y) {
        final Transform t;
        if (x < minX) {
            if (y < minY) {
                t = topLeft;
            } else if (y > maxY) {
                t = bottomLeft;
            } else {
                t = left;
            }
        } else if (x > maxX) {
            if (y < minY) {
                t = topRight;
            } else if (y > maxY) {
                t = bottomRight;
            } else {
                t = right;
            }
        } else {
            if (y < minY) {
                t = top;
            } else if (y > maxY) {
                t = bottom;
            } else {
                t = center;
            }
        }
        return t.transform(x, y);
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        Point2D p1 = transform(x1, y1);
        Point2D p2 = transform(x2, y2);
        Point2D p3 = transform(x3, y3);
        target.curveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
    }

    @Override
    protected void doLineTo(double x, double y) {
        Point2D p = transform(x, y);
        target.lineTo(p.getX(), p.getY());
    }

    @Override
    protected void doMoveTo(double x, double y) {
        Point2D p = transform(x, y);
        target.moveTo(p.getX(), p.getY());
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        Point2D p1 = transform(x1, y1);
        Point2D p2 = transform(x2, y2);
        target.quadTo(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    @Override
    protected void doPathDone() {
        target.pathDone();
    }

}
