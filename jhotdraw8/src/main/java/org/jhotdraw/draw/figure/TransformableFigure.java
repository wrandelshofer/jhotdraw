/* @(#)Figure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.SimpleFigureKey;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.Point3DStyleableMapAccessor;
import org.jhotdraw.draw.key.Scale3DStyleableMapAccessor;
import org.jhotdraw.draw.key.TransformListStyleableFigureKey;
import org.jhotdraw.geom.Geom;
import static org.jhotdraw.draw.figure.FigureImplementationDetails.*;

/**
 * A transformable figure supports the transformation of a figure.
 * <p>
 * The following transformations are supported:
 * <ul>
 * <li>Translation of the local bounds of the figure.</li>
 * <li>Rotation around the center of the untransformed local bounds of the
 * figure.</li>
 * <li>Scaling around the center of the untransformed local bounds of the
 * figure.</li>
 * <li>Arbitrary sequence of affine transformations of the figure.</li>
 * </ul>
 * Note that transformation matrices computed from the Rotation and Scaling must
 * be recomputed every time when the local bounds of the figure change.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface TransformableFigure extends TransformCacheableFigure {

    /**
     * Defines the angle of rotation around the center of the figure in degrees.
     * <p>
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey ROTATE = new DoubleStyleableFigureKey("rotate", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM), 0.0);
    /**
     * Defines the rotation axis used.
     * <p>
     * Default value: {@code Rotate.Z_AXIS}.
     */
    public static SimpleFigureKey<Point3D> ROTATION_AXIS = new SimpleFigureKey<>("rotationAxis", Point3D.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM), Rotate.Z_AXIS);
    /**
     * Defines the scale factor by which coordinates are scaled on the x axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_X = new DoubleStyleableFigureKey("scaleX", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the y axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_Y = new DoubleStyleableFigureKey("scaleY", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the z axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_Z = new DoubleStyleableFigureKey("scaleZ", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the axes
     * about the center of the figure.
     */
    public static Scale3DStyleableMapAccessor SCALE = new Scale3DStyleableMapAccessor("scale", SCALE_X, SCALE_Y, SCALE_Z);
    /**
     * Defines the translation on the x axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_X = new DoubleStyleableFigureKey("translateX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.TRANSFORM), 0.0);
    /**
     * Defines the translation on the y axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_Y = new DoubleStyleableFigureKey("translateY", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM), 0.0);
    /**
     * Defines the translation on the z axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_Z = new DoubleStyleableFigureKey("translateZ", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM), 0.0);
    /**
     * Defines the translation on the axes about the center of the figure.
     */
    public static Point3DStyleableMapAccessor TRANSLATE = new Point3DStyleableMapAccessor("translate", TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z);

    public static TransformListStyleableFigureKey TRANSFORM = new TransformListStyleableFigureKey("transform", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM), Collections.emptyList());

    /**
     * Updates a figure node with all transformation properties defined in this
     * interface.
     * <p>
     * Applies the following properties: {@code TRANSFORM}, translation
     * {@code TRANSLATE_X}, {@code TRANSLATE_Y}, {@code TRANSLATE_Z}, scale
     * {@code SCALE_X}, {@code SCALE_Y}, {@code SCALE_Z}, and rotation
     * {@code ROTATE}, {@code ROTATION_AXIS}.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applyTransformableFigureProperties(Node node) {
        Transform t = getLocalToParent();
        List<Transform> transforms = node.getTransforms();
        if (t.isIdentity()) {
            if (!transforms.isEmpty()) {
                transforms.clear();
            }
        } else if (transforms.size() == 1) {
            if (!Objects.equals(transforms.get(0), t)) {
                transforms.set(0, t);
            }
        } else {
            transforms.clear();
            transforms.add(t);
        }
    }

    default Transform getLocalToParentOld() {
        Transform t = CACHE ? get(FigureImplementationDetails.LOCAL_TO_PARENT) : null;
        if (t == null) {
            Point2D center = getCenterInLocal();
            Affine tx = new Affine();
            tx.appendTranslation(getStyled(TransformableFigure.TRANSLATE_X), getStyled(TransformableFigure.TRANSLATE_Y));
            tx.appendRotation(getStyled(TransformableFigure.ROTATE), center.getX(), center.getY());
            tx.appendScale(getStyled(TransformableFigure.SCALE_X), getStyled(TransformableFigure.SCALE_Y), center.getX(), center.getY());
            tx.append(getTransform());
            t = tx;
            if (CACHE) {
                set(FigureImplementationDetails.LOCAL_TO_PARENT, t);
            }
        }
        return t;
    }

    @Override
    default Transform getLocalToParent() {
        Transform t2l = CACHE ? get(FigureImplementationDetails.LOCAL_TO_PARENT) : null;
        if (t2l == null) {
            Point2D center = getCenterInLocal();

            double tx = getStyled(TRANSLATE_X);
            double ty = getStyled(TRANSLATE_Y);
            double r = getStyled(ROTATE);
            double sx = getStyled(SCALE_X);
            double sy = getStyled(SCALE_Y);
            List<Transform> t = getStyled(TRANSFORM);

            if (tx != 0.0 || ty != 0.0) {
                Translate tt = new Translate(tx, ty);
                t2l = t2l == null ? tt : t2l.createConcatenation(tt);
            }
            if (r != 0) {
                Rotate tr = new Rotate(r, center.getX(), center.getY());
                t2l = t2l == null ? tr : t2l.createConcatenation(tr);
            }
            if (sx != 1.0 || sy != 1.0) {
                Scale ts = new Scale(sx, sy, center.getX(), center.getY());
                t2l = t2l == null ? ts : t2l.createConcatenation(ts);
            }
            if (t != null && !t.isEmpty()) {
                t2l = t2l == null ? getTransform() : t2l.createConcatenation(getTransform());
            }
            if (t2l == null) {
                t2l = IDENTITY_TRANSFORM;
            }
            if (CACHE) {
                set(FigureImplementationDetails.PARENT_TO_LOCAL, t2l);
            }
        }
        return t2l;
    }

    /*
    @Override
    default Transform getParentToLocal() {
        Transform t = CACHE ? get(FigureImplementationDetails.PARENT_TO_LOCAL) : null;
        if (t == null) {
            Point2D center = getCenterInLocal();
            Affine tx = new Affine();
            tx.append(getInverseTransform());
            tx.appendScale(1.0 / getStyled(TransformableFigure.SCALE_X), 1.0 / getStyled(TransformableFigure.SCALE_Y), center.getX(), center.getY());
            tx.appendRotation(-getStyled(TransformableFigure.ROTATE), center.getX(), center.getY());
            tx.appendTranslation(-getStyled(TransformableFigure.TRANSLATE_X), -getStyled(TransformableFigure.TRANSLATE_Y));
            t = tx;
            if (CACHE) {
                set(FigureImplementationDetails.PARENT_TO_LOCAL, t);
            }
        }
        return t;
    }*/
    default Transform getParentToLocal() {
        Transform t2l = CACHE ? get(FigureImplementationDetails.LOCAL_TO_PARENT) : null;
        if (t2l == null) {
            Point2D center = getCenterInLocal();

            List<Transform> t = getStyled(TRANSFORM);
            double sx = getStyled(SCALE_X);
            double sy = getStyled(SCALE_Y);
            double r = getStyled(ROTATE);
            double tx = getStyled(TRANSLATE_X);
            double ty = getStyled(TRANSLATE_Y);

            if (t != null && !t.isEmpty()) {
                t2l = getInverseTransform();
            }
            if (sx != 1.0 || sy != 1.0) {
                Scale ts = new Scale(1.0 / sx, 1.0 / sy, center.getX(), center.getY());
                t2l = t2l == null ? ts : t2l.createConcatenation(ts);
            }
            if (r != 0) {
                Rotate tr = new Rotate(-r, center.getX(), center.getY());
                t2l = t2l == null ? tr : t2l.createConcatenation(tr);
            }
            if (tx != 0.0 || ty != 0.0) {
                Translate tt = new Translate(-tx, -ty);
                t2l = t2l == null ? tt : t2l.createConcatenation(tt);
            }
            if (t2l == null) {
                t2l = IDENTITY_TRANSFORM;
            }
            if (CACHE) {
                set(FigureImplementationDetails.PARENT_TO_LOCAL, t2l);
            }
        }
        return t2l;
    }

    default Transform getTransform() {
        List<Transform> list = getStyled(TRANSFORM);
        Transform t;
        if (list.isEmpty()) {
            t = FigureImplementationDetails.IDENTITY_TRANSFORM;
        } else {
            t = list.get(0);
            for (int i = 1, n = list.size(); i < n; i++) {
                t = t.createConcatenation(list.get(i));
            }
        }
        return t;
    }

    default Transform getInverseTransform() {
        List<Transform> list = getStyled(TRANSFORM);
        Transform t;
        if (list.isEmpty()) {
            t = FigureImplementationDetails.IDENTITY_TRANSFORM;
        } else {
            try {
                t = list.get(list.size() - 1).createInverse();
                for (int i = list.size() - 2; i >= 0; i--) {
                    t = t.createConcatenation(list.get(i).createInverse());
                }
            } catch (NonInvertibleTransformException e) {
                throw new InternalError(e);
            }
        }
        return t;
    }

    @Override
    default boolean invalidateTransforms() {
        if (!CACHE) {
            return false;
        }
        // intentional use of long-circuit or-expressions!!
        return TransformCacheableFigure.super.invalidateTransforms()
                | null != set(FigureImplementationDetails.PARENT_TO_LOCAL, null)
                | null != set(FigureImplementationDetails.LOCAL_TO_PARENT, null);
    }

}
