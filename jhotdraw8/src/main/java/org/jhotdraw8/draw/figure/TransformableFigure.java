/* @(#)Figure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.SimpleFigureKey;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.Point3DStyleableMapAccessor;
import org.jhotdraw8.draw.key.Scale3DStyleableMapAccessor;
import org.jhotdraw8.draw.key.TransformListStyleableFigureKey;
import static org.jhotdraw8.draw.figure.FigureImplementationDetails.*;

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
    public static DoubleStyleableFigureKey ROTATE = new DoubleStyleableFigureKey("rotate", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.DEPENDENT_LAYOUT), 0.0);
    /**
     * Defines the rotation axis used.
     * <p>
     * Default value: {@code Rotate.Z_AXIS}.
     */
    public static SimpleFigureKey<Point3D> ROTATION_AXIS = new SimpleFigureKey<>("rotationAxis", Point3D.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.DEPENDENT_LAYOUT), Rotate.Z_AXIS);
    /**
     * Defines the scale factor by which coordinates are scaled on the x axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_X = new DoubleStyleableFigureKey("scaleX", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.DEPENDENT_LAYOUT), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the y axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_Y = new DoubleStyleableFigureKey("scaleY", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.DEPENDENT_LAYOUT), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the z axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_Z = new DoubleStyleableFigureKey("scaleZ", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.DEPENDENT_LAYOUT), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the axes
     * about the center of the figure.
     */
    public static Scale3DStyleableMapAccessor SCALE = new Scale3DStyleableMapAccessor("scale", SCALE_X, SCALE_Y, SCALE_Z);
    /**
     * Defines the translation on the x axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_X = new DoubleStyleableFigureKey("translateX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.TRANSFORM, DirtyBits.DEPENDENT_LAYOUT), 0.0);
    /**
     * Defines the translation on the y axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_Y = new DoubleStyleableFigureKey("translateY", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.DEPENDENT_LAYOUT), 0.0);
    /**
     * Defines the translation on the z axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_Z = new DoubleStyleableFigureKey("translateZ", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.DEPENDENT_LAYOUT), 0.0);
    /**
     * Defines the translation on the axes about the center of the figure.
     */
    public static Point3DStyleableMapAccessor TRANSLATE = new Point3DStyleableMapAccessor("translate", TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z);

    public static TransformListStyleableFigureKey TRANSFORMS = new TransformListStyleableFigureKey("transform", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.DEPENDENT_LAYOUT), Collections.emptyList());

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
    default void applyTransformableFigurePropertiesFast(Node node) {
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

    default void applyTransformableFigureProperties(Node node) {
        double tx = getStyled(TRANSLATE_X);
        double ty = getStyled(TRANSLATE_Y);
        double r = getStyled(ROTATE);
        double sx = getStyled(SCALE_X);
        double sy = getStyled(SCALE_Y);
        List<Transform> t = getStyled(TRANSFORMS);
        node.setTranslateX(tx);
        node.setTranslateY(ty);
        node.setRotate(r);
        node.setScaleX(sx);
        node.setScaleY(sy);
        node.getTransforms().setAll(t);
    }

    @Override
    default Transform getLocalToParent() {
        return getLocalToParent(true);
    }

    default Transform getLocalToParent(boolean styled) {
        Transform l2p = CACHE && styled ? get(FigureImplementationDetails.LOCAL_TO_PARENT) : null;
        if (l2p == null) {
            Point2D center = getCenterInLocal();

            List<Transform> t = styled ? getStyled(TRANSFORMS) : get(TRANSFORMS);
            double sx = styled ? getStyled(SCALE_X) : get(SCALE_X);
            double sy = styled ? getStyled(SCALE_Y) : get(SCALE_Y);
            double r = styled ? getStyled(ROTATE) : get(ROTATE);
            double tx = styled ? getStyled(TRANSLATE_X) : get(TRANSLATE_X);
            double ty = styled ? getStyled(TRANSLATE_Y) : get(TRANSLATE_Y);

            if (tx != 0.0 || ty != 0.0) {
                Translate tt = new Translate(tx, ty);
                l2p = l2p == null ? tt : l2p.createConcatenation(tt);
            }
            if (r != 0) {
                Rotate tr = new Rotate(r, center.getX(), center.getY());
                l2p = l2p == null ? tr : l2p.createConcatenation(tr);
            }
            if ((sx != 1.0 || sy != 1.0) && sx != 0.0 && sy != 0.0) {// check for 0.0 avoids creating a non-invertible transform
                Scale ts = new Scale(sx, sy, center.getX(), center.getY());
                l2p = l2p == null ? ts : l2p.createConcatenation(ts);
            }
            if (t != null && !t.isEmpty()) {
                l2p = l2p == null ? getTransform() : l2p.createConcatenation(getTransform());
            }
            if (l2p == null) {
                l2p = IDENTITY_TRANSFORM;
            }
            if (CACHE && styled) {
                set(FigureImplementationDetails.PARENT_TO_LOCAL, l2p);
            }
        }
        return l2p;
    }

    default List<Transform> getLocalToParentAsList(boolean styled) {
        ArrayList<Transform> list = new ArrayList<>();

        Point2D center = getCenterInLocal();

        List<Transform> t = styled ? getStyled(TRANSFORMS) : get(TRANSFORMS);
        double sx = styled ? getStyled(SCALE_X) : get(SCALE_X);
        double sy = styled ? getStyled(SCALE_Y) : get(SCALE_Y);
        double r = styled ? getStyled(ROTATE) : get(ROTATE);
        double tx = styled ? getStyled(TRANSLATE_X) : get(TRANSLATE_X);
        double ty = styled ? getStyled(TRANSLATE_Y) : get(TRANSLATE_Y);

        if (tx != 0.0 || ty != 0.0) {
            Translate tt = new Translate(tx, ty);
            list.add(tt);
        }
        if (r != 0) {
            Rotate tr = new Rotate(r, center.getX(), center.getY());
            list.add(tr);
        }
        if ((sx != 1.0 || sy != 1.0) && sx != 0.0 && sy != 0.0) {// check for 0.0 avoids creating a non-invertible transform
            Scale ts = new Scale(sx, sy, center.getX(), center.getY());
            list.add(ts);
        }
        if (t != null && !t.isEmpty()) {
            list.addAll(t);
        }
        return list;
    }

    default boolean hasTransforms() {
        return !get(TRANSFORMS).isEmpty();
    }

    default boolean hasCenterTransforms() {
        double sx = getStyled(SCALE_X);
        double sy = getStyled(SCALE_Y);
        double r = getStyled(ROTATE);
        double tx = getStyled(TRANSLATE_X);
        double ty = getStyled(TRANSLATE_Y);
        return sx != 1 || sy != 1 || r != 0 || tx != 0 || ty != 0;
    }

    @Override
    default void reshapeInLocal(Transform transform) {
        if (hasCenterTransforms()) {
            ArrayList<Transform> ts = new ArrayList<>(get(TRANSFORMS));
            if (ts.isEmpty()) {
                ts.add(0, transform);
            } else {
                int last = ts.size() - 1;
                Transform concatenatedWithLast = ts.get(last).createConcatenation(transform);
                if (concatenatedWithLast instanceof Affine) {
                    ts.add(transform);
                } else {
                    ts.set(last, concatenatedWithLast);
                }
            }
            set(TRANSFORMS, ts);
            return;
        }

        Bounds b = getBoundsInLocal();
        b = transform.transform(b);
        reshape(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    default void reshapeInParent(Transform transform) {
        if (hasCenterTransforms() || hasTransforms()) {
            if (transform instanceof Translate) {
                Translate translate = (Translate) transform;
                set(TRANSLATE_X, get(TRANSLATE_X) + translate.getTx());
                set(TRANSLATE_Y, get(TRANSLATE_Y) + translate.getTy());
            } else {
                flattenTransforms();
                List<Transform> transforms = get(TRANSFORMS);
                if (transforms.isEmpty()) {
                    set(TRANSFORMS, Collections.singletonList(transform));
                } else {
                    ArrayList<Transform> newTransforms = new ArrayList<>();
                    newTransforms.add(transform);
                    newTransforms.addAll(transforms);
                    set(TRANSFORMS, newTransforms);
                }
            }
        } else {
            reshapeInLocal(getParentToLocal().createConcatenation(transform));
        }
    }

    @Override
    default void transformInParent(Transform t) {
        if (t == null || t.isIdentity()) {
            return;
        }
        if (t instanceof Translate) {
            Translate tr = (Translate) t;
            set(TRANSLATE_X, get(TRANSLATE_X) + tr.getTx());
            set(TRANSLATE_Y, get(TRANSLATE_Y) + tr.getTy());
            return;
        } else {
            flattenTransforms();
            List<Transform> transforms = new ArrayList<>(get(TRANSFORMS));
            if (transforms.isEmpty()) {
                set(TRANSFORMS, Collections.singletonList(t));
            } else {
                transforms.add(0, t);
                set(TRANSFORMS, transforms);
            }
        }
    }

    @Override
    default void transformInLocal(Transform t) {
        flattenTransforms();
        List<Transform> transforms = get(TRANSFORMS);
        if (transforms.isEmpty()) {
            set(TRANSFORMS, Collections.singletonList(t));
        } else {
            transforms.add(t);
            set(TRANSFORMS, transforms);
        }
    }

    default void flattenTransforms() {
        Transform p2l = getLocalToParent(false);
        set(SCALE_X, 1.0);
        set(SCALE_Y, 1.0);
        set(ROTATE, 0.0);
        set(TRANSLATE_X, 0.0);
        set(TRANSLATE_Y, 0.0);
        if (p2l.isIdentity()) {
            set(TRANSFORMS, Collections.emptyList());
        } else {
            set(TRANSFORMS, Collections.singletonList(p2l));
        }
    }

    default void clearTransforms() {
        set(SCALE_X, 1.0);
        set(SCALE_Y, 1.0);
        set(ROTATE, 0.0);
        set(TRANSLATE_X, 0.0);
        set(TRANSLATE_Y, 0.0);
        set(TRANSFORMS, Collections.emptyList());
    }

    default Transform getParentToLocal() {
        return getParentToLocal(true);
    }

    default Transform getParentToLocal(boolean styled) {
        Transform p2l = CACHE ? get(FigureImplementationDetails.LOCAL_TO_PARENT) : null;
        if (p2l == null) {
            Point2D center = getCenterInLocal();

            List<Transform> t = styled ? getStyled(TRANSFORMS) : get(TRANSFORMS);
            double sx = styled ? getStyled(SCALE_X) : get(SCALE_X);
            double sy = styled ? getStyled(SCALE_Y) : get(SCALE_Y);
            double r = styled ? getStyled(ROTATE) : get(ROTATE);
            double tx = styled ? getStyled(TRANSLATE_X) : get(TRANSLATE_X);
            double ty = styled ? getStyled(TRANSLATE_Y) : get(TRANSLATE_Y);

            if (t != null && !t.isEmpty()) {
                p2l = getInverseTransform();
            }
            if ((sx != 1.0 || sy != 1.0) && sx != 0.0 && sy != 0.0) {// check for 0.0 avoids creating a non-invertible transform
                Scale ts = new Scale(1.0 / sx, 1.0 / sy, center.getX(), center.getY());
                p2l = p2l == null ? ts : p2l.createConcatenation(ts);
            }
            if (r != 0) {
                Rotate tr = new Rotate(-r, center.getX(), center.getY());
                p2l = p2l == null ? tr : p2l.createConcatenation(tr);
            }
            if (tx != 0.0 || ty != 0.0) {
                Translate tt = new Translate(-tx, -ty);
                p2l = p2l == null ? tt : p2l.createConcatenation(tt);
            }
            if (p2l == null) {
                p2l = IDENTITY_TRANSFORM;
            }
            if (CACHE) {
                set(FigureImplementationDetails.PARENT_TO_LOCAL, p2l);
            }
        }
        return p2l;
    }

    default Transform getTransform() {
        List<Transform> list = getStyled(TRANSFORMS);
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
        List<Transform> list = getStyled(TRANSFORMS);
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

    public static List<Key<?>> getDeclaredKeys() {
        List<Key<?>> keys = new ArrayList<>();
        for (Field f : TransformableFigure.class.getDeclaredFields()) {
            if (Key.class.isAssignableFrom(f.getType())) {
                try {
                    keys.add((Key) f.get(null));
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new InternalError(ex);
                }
            }
        }
        return keys;
    }
}
