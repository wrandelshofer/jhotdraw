/* @(#)Transforms.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.geom;

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

/**
 * Transforms.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class Transforms {

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
    
        public static Transform concat(Transform a, Transform b) {
       return (a==null) ? b : (b==null?a:a.createConcatenation(b));
    }
                public static Transform concat(Transform a, Transform b, Transform c) {
       return concat(concat(a,b),c);
    }

    public static Bounds transform(Transform tx, Bounds b) {
        return tx==null?b:tx.transform(b);
    }
}
