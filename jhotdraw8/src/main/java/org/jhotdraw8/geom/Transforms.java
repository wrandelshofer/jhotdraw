/* @(#)Transforms.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.geom;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
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

/**
 * Transforms.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class Transforms {

    public static Transform concat(Transform a, Transform b) {
        return (a == null) ? b : (b == null ? a : a.createConcatenation(b));
    }

    public static Transform concat(Transform a, Transform b, Transform c) {
        return concat(concat(a, b), c);
    }

    public static Transform createReshapeTransform(Bounds f, Bounds t) {
        return createReshapeTransform(
                f.getMinX(), f.getMinY(), f.getWidth(), f.getHeight(),
                t.getMinX(), t.getMinY(), t.getWidth(), t.getHeight()
        );
    }

    public static Transform createReshapeTransform(double fx, double fy, double fw, double fh, double tx, double ty, double tw, double th) {
        double sx = tw / fw;
        double sy = th / fh;

        Transform t = new Translate(tx - fx, ty - fy);
        if (!Double.isNaN(sx) && !Double.isNaN(sy)
                && !Double.isInfinite(sx) && !Double.isInfinite(sy)
                && (sx != 1d || sy != 1d)) {
            t = Transforms.concat(t, new Scale(sx, sy, fx, fy));
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

    public static Point2D deltaTransform(Transform t, double x, double y) {
        if (t == null) {
            return new Point2D(x, y);
        } else {
            return t.deltaTransform(x, y);
        }
    }

    public static Point2D deltaTransform(Transform t, Point2D p) {
        if (t == null) {
            return p;
        } else {
            return t.deltaTransform(p);
        }
    }

    public static AffineTransform toAWT(Transform t) {
        if (t == null) {
            return null;
        }
        return new AffineTransform(t.getMxx(), t.getMyx(), t.getMxy(), t.getMyy(), t.getTx(), t.getTy());
    }

    public static Bounds transform(Transform tx, Bounds b) {
        return tx == null ? b : tx.transform(b);
    }

    public static Point2D transform(Transform tx, Point2D b) {
        return tx == null ? b : tx.transform(b);
    }

    public static Point2D transform(Transform tx, double x, double y) {
        return tx == null ? new Point2D(x, y) : tx.transform(x, y);
    }

}
