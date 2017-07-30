/* @(#)NineSliceScalingBuilder.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

/**
 * This builder slices the input path into 9 pieces, and scales them by the
 * specified scale factor and pivot.
 * <p>
 * The builder takes a four values minx, miny, maxx, maxy as input, or a
 * rectangle and insets from which these four values are computed.
 * <p>
 * The coordinate space is split along minx, miny, maxx, maxy into 9 pieces:
 * top-left, top-right, bottom-right, bottom-left, top, right, bottom, left,
 * center.
 * <p>
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
 * @version $$Id$$
 */
public class NineSliceScalingBuilder extends AbstractPathBuilder {

    private final double minX, minY, maxX, maxY;
    private final Transform topLeft, topRight, bottomRight, bottomLeft, top, right, bottom, left, center;
    private final PathBuilder target;

    public NineSliceScalingBuilder(PathBuilder target, double sx, double sy, double pivotX, double pivotY, double minX, double minY, double maxX, double maxY) {
        this.target=target;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        center = new Scale(sx, sy, pivotX, pivotY);
        top = new Scale(sx, 1, pivotX, 0).createConcatenation(new Translate(0, (minY - pivotY) * sy));
        bottom = new Scale(sx, 1, pivotX, 0).createConcatenation(new Translate(0, (maxY - pivotY) * sy));
        right = new Scale(1, sy, 0, pivotY).createConcatenation(new Translate((maxX - pivotX) * sx, 0));
        left = new Scale(1, sy, 0, pivotY).createConcatenation(new Translate((minX - pivotX) * sx, 0));
        topLeft = new Scale(sx, sy, pivotX, pivotY).createConcatenation(new Translate((minX - pivotX * sx), (minY - pivotY) * sy));
        topRight = new Scale(sx, sy, pivotX, pivotY).createConcatenation(new Translate((maxX - pivotX * sx), (minY - pivotY) * sy));
        bottomRight = new Scale(sx, sy, pivotX, pivotY).createConcatenation(new Translate((minX - pivotX * sx), (maxY - pivotY) * sy));
        bottomLeft = new Scale(sx, sy, pivotX, pivotY).createConcatenation(new Translate((minX - pivotX * sx), (maxY - pivotY) * sy));
    }

    public NineSliceScalingBuilder(PathBuilder target, double sx, double sy, double pivotX, double pivotY, Bounds bounds, Insets insets) {
        this(target, sx, sy, pivotX, pivotY,
                bounds.getMinX() + insets.getLeft(),
                bounds.getMinY() + insets.getTop(),
                bounds.getMaxX() - insets.getRight(),
                bounds.getMaxY() - insets.getBottom()
        );

    }

    @Override
    protected void doClosePath() {
        target.closePath();
    }
    
    private Point2D transform(double x, double y) {
        if (x< minX) {
            
        }else if (x>maxX) {
            
        }else{
            
        }
        return new Point2D(x,y);
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        Point2D p1=transform(x1,y1);
        Point2D p2=transform(x2,y2);
        Point2D p3=transform(x3,y3);
        target.curveTo(p1.getX(),p1.getY(),p2.getX(),p2.getY(),p3.getX(),p3.getY());
    }

    @Override
    protected void doLineTo(double x, double y) {
        Point2D p=transform(x,y);
        target.lineTo(p.getX(),p.getY());
    }

    @Override
    protected void doMoveTo(double x, double y) {
        Point2D p=transform(x,y);
        target.moveTo(p.getX(),p.getY());
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        Point2D p1=transform(x1,y1);
        Point2D p2=transform(x2,y2);
        target.quadTo(p1.getX(),p1.getY(),p2.getX(),p2.getY());
    }
    @Override
    protected void doFinish() {
       target.finish();
    }


}
