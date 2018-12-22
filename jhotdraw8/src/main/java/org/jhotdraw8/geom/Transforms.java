/* @(#)Transforms.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import static java.lang.Math.sqrt;
import static java.lang.Math.atan;
import static java.lang.Math.abs;
import static java.lang.Double.isNaN;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.UnitConverter;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

/**
 * Transforms.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Transforms {
    /**
     * Immutable identity transform.
     */
    public static final Transform IDENTITY = new Translate();

    @Nonnull
    public static Transform concat(@Nullable Transform a, @Nullable Transform b) {
        Transform t = (a == null || a.isIdentity()) ? b : (b == null || b.isIdentity() ? a : a.createConcatenation(b));
        return t == null ? IDENTITY : t;
    }


    @Nonnull
    public static Transform createReshapeTransform(Bounds src, Bounds dest) {
        return createReshapeTransform(
                src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight(),
                dest.getMinX(), dest.getMinY(), dest.getWidth(), dest.getHeight()
        );
    }

    @Nonnull
    public static Transform createReshapeTransform(Bounds src, double destX, double destY, double destW, double destH) {
        return createReshapeTransform(
                src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight(),
                destX, destY, destW, destH
        );
    }
    @Nonnull
    public static Transform createReshapeTransform(Rectangle2D src, double destX, double destY, double destW, double destH) {
        return createReshapeTransform(
                src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight(),
                destX, destY, destW, destH
        );
    }
    @Nonnull
    public static Transform createReshapeTransform(CssRectangle2D csssrc, CssSize destX, CssSize destY, CssSize destW, CssSize destH) {
        return createReshapeTransform(csssrc.getConvertedValue(),
                destX.getConvertedValue(), destY.getConvertedValue(), destW.getConvertedValue(), destH.getConvertedValue()
        );
    }

    @Nonnull
    static Transform createReshapeTransform(double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh) {
        double scaleX = dw / sw;
        double scaleY = dh / sh;

        Transform t = new Translate(dx - sx, dy - sy);
        if (!Double.isNaN(scaleX) && !Double.isNaN(scaleY)
                && !Double.isInfinite(scaleX) && !Double.isInfinite(scaleY)
                && (scaleX != 1d || scaleY != 1d)) {
            t = Transforms.concat(t, new Scale(scaleX, scaleY, sx, sy));
        }
        return t;
    }

    /**
     * Decomposes the given transformation matrix into rotation, followed by
     * scale and then translation. Returns the matrix if the decomposition
     * fails. Returns an empty list if the transform is the identity matrix.
     *
     * @param transform a transformation
     * @return decomposed transformation
     */
    @Nonnull
    public static List<Transform> decompose(Transform transform) {
        List<Transform> list = new ArrayList<>();
        if (transform.isIdentity()) {
        } else if (transform instanceof Translate) {
            list.add(transform);
        } else if (transform instanceof Scale) {
            list.add(transform);
        } else if (transform instanceof Rotate) {
            list.add(transform);
        } else {

            // xx the X coordinate scaling element of the 3x4 matrix
            // yx the Y coordinate shearing element of the 3x4 matrix
            // xy the X coordinate shearing element of the 3x4 matrix
            // yy the Y coordinate scaling element of the 3x4 matrix
            // tx the X coordinate translation element of the 3x4 matrix
            // ty the Y coordinate translation element of the 3x4 matrix
            //      [ xx xy tx ]    [ a b tx ]
            //      [ yx yy ty  ] =[ c d ty ] 
            //       [  0  0  1  ]  [ 0 0 1 ]
            double a = transform.getMxx();
            double b = transform.getMxy();
            double c = transform.getMyx();
            double d = transform.getMyy();
            double tx = transform.getTx();
            double ty = transform.getTy();

            double sx = sqrt(a * a + c * c);
            double sy = sqrt(b * b + d * d);

            double rot1 = atan(c / d);
            double rot2 = atan(-b / a);

            if (isNaN(rot1) || isNaN(rot2) || abs(rot1 - rot2) > 1e-6) {
                list.add(transform);
                return list;
            }

            if (tx != 0.0 || ty != 0.0) {
                list.add(new Translate(tx, ty));
            }
            if (sx != 1.0 || sy != 1.0) {
                list.add(new Scale(tx, ty));
            }
            if (rot1 != 0.0 && rot2 != 0.0) {
                list.add(new Rotate(rot1 * 180.0 / Math.PI));
            }
        }

        return list;
    }

    public static Point2D deltaTransform(@Nullable Transform t, double x, double y) {
        if (t == null) {
            return new Point2D(x, y);
        } else {
            return t.deltaTransform(x, y);
        }
    }

    public static Point2D deltaTransform(@Nullable Transform t, @Nonnull Point2D p) {
        if (t == null) {
            return p;
        } else {
            return t.deltaTransform(p);
        }
    }

    @Nullable
    public static AffineTransform toAWT(@Nullable Transform t) {
        if (t == null) {
            return null;
        }
        return new AffineTransform(t.getMxx(), t.getMyx(), t.getMxy(), t.getMyy(), t.getTx(), t.getTy());
    }

    @Nonnull
    public static Bounds transform(@Nullable Transform tx, @Nonnull Bounds b) {
        return tx == null ? b : tx.transform(b);
    }

    @Nonnull
    public static Point2D transform(@Nullable Transform tx, @Nonnull Point2D b) {
        return tx == null ? b : tx.transform(b);
    }

    @Nonnull
    public static Point2D transform(@Nullable Transform tx, double x, double y) {
        return tx == null ? new Point2D(x, y) : tx.transform(x, y);
    }

    /**
     * Rotates from tangent vector.
     * <p>
     * A tangent vector pointing to (1,0) results in an identity matrix.
     * <p>
     *
     * @param tangent a tangent vector
     * @param pivot   the pivot of the rotation
     * @return a rotation transform
     */
    public static Transform rotate(Point2D tangent, Point2D pivot) {
        double theta = Math.atan2(tangent.getY(), tangent.getX());
        return Transform.rotate(theta * 180.0 / Math.PI, pivot.getX(), pivot.getY());
    }

    /**
     * Rotates from tangent vector.
     * <p>
     * A tangent vector pointing to (1,0) results in an identity matrix.
     * <p>
     *
     * @param tangentX a tangent vector
     * @param tangentY a tangent vector
     * @param pivotX   the pivot of the rotation
     * @param pivotY   the pivot of the rotation
     * @return a rotation transform
     */
    public static Transform rotate(double tangentX, double tangentY, double pivotX, double pivotY) {
        double theta = Math.atan2(tangentY, tangentX);
        return Transform.rotate(theta * 180.0 / Math.PI, pivotX, pivotY);
    }

    /**
     * Creates a transformation matrix, which projects a point onto the given line.
     * The projection is orthogonal to the line.
     * The point will not be clipped off by the line.
     * <p>
     * Formula: b = project(a, p1,p2)
     * <pre>
     *  v = p2 - p1;
     *  b = vvT / vTv * (a - p1) + p1;
     *  b = [ vvT / vTv | vvT / vTv * p1 ] * a; // 2 by 3 matrix
     * </pre>
     *
     * @param x1 x-coordinate of p1 of the line
     * @param y1 y-coordinate of p1 of the line
     * @param x2 x-coordinate of p2 of the line
     * @param y2 y-coordinate of p2 of the line
     * @return the transformation matrix
     */
    @Nonnull
    public static Transform createProjectPointOnLineTransform(double x1, double y1, double x2, double y2) {
        double vx = x2 - x1;
        double vy = y2 - y1;
        double vxx = vx * vx;
        double vyy = vy * vy;
        double vTv = vxx + vyy;

        double xx = vxx / vTv;
        double xy = vx * vy / vTv;
        double yy = vyy / vTv;
        double yx = xy;
        double tx = xx * x1 + xy * y1;
        double ty = yx * x1 + yy * y1;

        return new Affine(xx, xy, tx, yx, yy, ty);
    }

    @Nonnull
    public static Point2D projectPointOnLine(double ax, double ay, double x1, double y1, double x2, double y2) {
        double vx = x2 - x1;
        double vy = y2 - y1;
        double vxx = vx * vx;
        double vyy = vy * vy;
        double vTv = vxx + vyy;

        double xx = vxx / vTv;
        double xy = vx * vy / vTv;
        double yy = vyy / vTv;
        double yx = xy;
        double tx = xx * x1 + xy * y1;
        double ty = yx * x1 + yy * y1;


        double bx = xx * ax + xy * ay + tx;
        double by = yx * ax + yy * ay + ty;
        return new Point2D(bx, by);
    }

    public static CssRectangle2D transform(Transform transform, CssRectangle2D b) {
        Bounds tb = transform.transform(b.getConvertedBoundsValue());
        DefaultUnitConverter c = DefaultUnitConverter.getInstance();
        return new CssRectangle2D(
                c.convertSize(tb.getMinX(), UnitConverter.DEFAULT,b.getMinX().getUnits()),
                c.convertSize(tb.getMinY(),UnitConverter.DEFAULT,b.getMinY().getUnits()),
                c.convertSize(tb.getWidth(),UnitConverter.DEFAULT,b.getWidth().getUnits()),
                c.convertSize(tb.getHeight(),UnitConverter.DEFAULT,b.getHeight().getUnits())
                );
    }

    public static boolean isIdentityOrNull(Transform t) {
        return t==null||t.isIdentity();
    }
}
