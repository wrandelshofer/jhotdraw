/* @(#)Figure.java
 * Copyright © by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javax.annotation.Nonnull;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssTranslate3DConverterOLD;
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
import javax.annotation.Nullable;

import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.Point3DStyleableMapAccessor;
import org.jhotdraw8.draw.key.Scale3DStyleableMapAccessor;
import org.jhotdraw8.draw.key.TransformListStyleableFigureKey;
import static org.jhotdraw8.draw.figure.FigureImplementationDetails.*;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Transforms;

/**
 * A transformable figure supports the transformation of a figure.
 * <p>
 * The following transformations are supported:
 * <ul>
 * <li>Translation ofCollection the local bounds ofCollection the figure.</li>
 * <li>Rotation around the center ofCollection the untransformed local bounds
 * ofCollection the figure.</li>
 * <li>Scaling around the center ofCollection the untransformed local bounds
 * ofCollection the figure.</li>
 * <li>Arbitrary sequence ofCollection affine transformations ofCollection the
 * figure.</li>
 * </ul>
 * Note that transformation matrices computed from the Rotation and Scaling must
 * be recomputed every time when the local bounds ofCollection the figure
 * change.
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
    public static DoubleStyleableFigureKey ROTATE = new DoubleStyleableFigureKey("rotate", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.LAYOUT_OBSERVERS), 0.0);
    /**
     * Defines the rotation axis used.
     * <p>
     * Default value: {@code Rotate.Z_AXIS}.
     */
    public static SimpleFigureKey<Point3D> ROTATION_AXIS = new SimpleFigureKey<>("rotationAxis", Point3D.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.LAYOUT_OBSERVERS), Rotate.Z_AXIS);
    /**
     * Defines the scale factor by which coordinates are scaled on the x axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_X = new DoubleStyleableFigureKey("scaleX", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.LAYOUT_OBSERVERS), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the y axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_Y = new DoubleStyleableFigureKey("scaleY", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.LAYOUT_OBSERVERS), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the z axis
     * about the center of the figure. Default value: {@code 1}.
     */
    public static DoubleStyleableFigureKey SCALE_Z = new DoubleStyleableFigureKey("scaleZ", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.LAYOUT_OBSERVERS), 1.0);
    /**
     * Defines the scale factor by which coordinates are scaled on the axes
     * about the center ofCollection the figure.
     */
    public static Scale3DStyleableMapAccessor SCALE = new Scale3DStyleableMapAccessor("scale", SCALE_X, SCALE_Y, SCALE_Z);
    public static TransformListStyleableFigureKey TRANSFORMS = new TransformListStyleableFigureKey("transform", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.LAYOUT_OBSERVERS), ImmutableList.emptyList());
    /**
     * Defines the translation on the x axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_X = new DoubleStyleableFigureKey("translateX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.TRANSFORM, DirtyBits.LAYOUT_OBSERVERS), 0.0);
    /**
     * Defines the translation on the y axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_Y = new DoubleStyleableFigureKey("translateY", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.LAYOUT_OBSERVERS), 0.0);
    /**
     * Defines the translation on the z axis about the center of the figure.
     * Default value: {@code 0}.
     */
    public static DoubleStyleableFigureKey TRANSLATE_Z = new DoubleStyleableFigureKey("translateZ", DirtyMask.of(DirtyBits.NODE, DirtyBits.TRANSFORM, DirtyBits.LAYOUT_OBSERVERS), 0.0);
    /**
     * Defines the translation on the axes about the center ofCollection the
     * figure.
     */
    public static Point3DStyleableMapAccessor TRANSLATE = new Point3DStyleableMapAccessor("translate", TRANSLATE_X, TRANSLATE_Y, TRANSLATE_Z, new CssTranslate3DConverterOLD(false));

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
     *  @param ctx
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applyTransformableFigureProperties(@Nonnull RenderContext ctx, @Nonnull Node node) {
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

    default void clearTransforms() {
        set(SCALE_X, 1.0);
        set(SCALE_Y, 1.0);
        set(ROTATE, 0.0);
        set(TRANSLATE_X, 0.0);
        set(TRANSLATE_Y, 0.0);
        set(TRANSFORMS, ImmutableList.of());
    }

    default void flattenTransforms() {
        Transform p2l = getLocalToParent(false);
        set(SCALE_X, 1.0);
        set(SCALE_Y, 1.0);
        set(ROTATE, 0.0);
        set(TRANSLATE_X, 0.0);
        set(TRANSLATE_Y, 0.0);
        if (p2l.isIdentity()) {
            set(TRANSFORMS, ImmutableList.emptyList());
        } else {
            set(TRANSFORMS, ImmutableList.of(p2l));
        }
    }

    @javax.annotation.Nullable
    default Transform getInverseTransform() {
        ImmutableList<Transform> list = getStyled(TRANSFORMS);
        Transform t;
        if (list.isEmpty()) {
            t = null; // leave null
        } else {
            try {
                t = list.get(list.size() - 1).createInverse();
                for (int i = list.size() - 2; i >= 0; i--) {
                    t = Transforms.concat(t, list.get(i).createInverse());
                }
            } catch (NonInvertibleTransformException e) {
                throw new InternalError(e);
            }
        }
        return t;
    }

    @javax.annotation.Nullable
    @Override
    default Transform getLocalToParent() {
        return getLocalToParent(true);
    }

    @javax.annotation.Nullable
    default Transform getLocalToParent(boolean styled) {
        Transform l2p = CACHE && styled ? getCachedValue(FigureImplementationDetails.LOCAL_TO_PARENT) : null;
        if (l2p == null) {
            Point2D center = getCenterInLocal();

            ImmutableList<Transform> t = styled ? getStyled(TRANSFORMS) : get(TRANSFORMS);
            double sx = styled ? getStyled(SCALE_X) : get(SCALE_X);
            double sy = styled ? getStyled(SCALE_Y) : get(SCALE_Y);
            double r = styled ? getStyled(ROTATE) : get(ROTATE);
            double tx = styled ? getStyled(TRANSLATE_X) : get(TRANSLATE_X);
            double ty = styled ? getStyled(TRANSLATE_Y) : get(TRANSLATE_Y);

            if (tx != 0.0 || ty != 0.0) {
                Translate tt = new Translate(tx, ty);
                l2p = Transforms.concat(l2p, tt);
            }
            if (r != 0) {
                Rotate tr = new Rotate(r, center.getX(), center.getY());
                l2p = Transforms.concat(l2p, tr);
            }
            if ((sx != 1.0 || sy != 1.0) && sx != 0.0 && sy != 0.0) {// check for 0.0 avoids creating a non-invertible transform
                Scale ts = new Scale(sx, sy, center.getX(), center.getY());
                l2p = Transforms.concat(l2p, ts);
            }
            if (t != null && !t.isEmpty()) {
                l2p = Transforms.concat(l2p, getTransform());
            }
            if (l2p == null) {
                l2p = IDENTITY_TRANSFORM;
            }
            if (CACHE && styled) {
                setCachedValue(FigureImplementationDetails.LOCAL_TO_PARENT, l2p);
            }
        }
        return l2p;
    }

        @Nonnull
        default List<Transform> getLocalToParentAsList(boolean styled) {
        ArrayList<Transform> list = new ArrayList<>();

        Point2D center = getCenterInLocal();

        ImmutableList<Transform> t = styled ? getStyledNonnull(TRANSFORMS) : getNonnull(TRANSFORMS);
        double sx = styled ? getStyledNonnull(SCALE_X) : getNonnull(SCALE_X);
        double sy = styled ? getStyledNonnull(SCALE_Y) : getNonnull(SCALE_Y);
        double r = styled ? getStyledNonnull(ROTATE) : getNonnull(ROTATE);
        double tx = styled ? getStyledNonnull(TRANSLATE_X) : getNonnull(TRANSLATE_X);
        double ty = styled ? getStyledNonnull(TRANSLATE_Y) : getNonnull(TRANSLATE_Y);

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
        if ( !t.isEmpty()) {
            list.addAll(t.asList());
        }
        return list;
    }

    /**
     * Returns null if identity.
     */
    @Nullable
    default Transform getParentToLocal() {
        return getParentToLocal(true);
    }

    /**
     * Returns null if identity.
     *
     * @param styled whether the styled value should be used
     * @return the transform or null
     */
    @Nullable
    default Transform getParentToLocal(boolean styled) {
        Transform p2l = CACHE ? getCachedValue(FigureImplementationDetails.PARENT_TO_LOCAL) : null;
        if (p2l == null) {
            Point2D center = getCenterInLocal();

            ImmutableList<Transform> t = styled ? getStyled(TRANSFORMS) : get(TRANSFORMS);
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
                p2l = Transforms.concat(p2l, ts);
            }
            if (r != 0) {
                Rotate tr = new Rotate(-r, center.getX(), center.getY());
                p2l = Transforms.concat(p2l, tr);
            }
            if (tx != 0.0 || ty != 0.0) {
                Translate tt = new Translate(-tx, -ty);
                p2l = Transforms.concat(p2l, tt);
            }
            if (p2l == null) {
                // KEEP IT NULL - muahaha
                //p2l = IDENTITY_TRANSFORM;
            }
            if (CACHE) {
                setCachedValue(FigureImplementationDetails.PARENT_TO_LOCAL, p2l);
            }
        }
        return p2l;
    }

    @Nullable
    default Transform getTransform() {
        ImmutableList<Transform> list = getStyled(TRANSFORMS);
        Transform t;
        if (list.isEmpty()) {
            t = null; // leave empty
        } else {
            t = list.get(0);
            for (int i = 1, n = list.size(); i < n; i++) {
                t = Transforms.concat(t, list.get(i));
            }
        }
        return t;
    }

    /**
     * Convenience method for setting a new value for the {@link#TRANSFORMS}
     * property.
     *
     * @param transforms new value
     */
    default void setTransforms(@Nonnull Transform... transforms) {
        if (transforms.length == 1 && transforms[0].isIdentity()) {
            set(TRANSFORMS, ImmutableList.emptyList());
        } else {
            set(TRANSFORMS, ImmutableList.of(transforms));
        }
    }

    default boolean hasCenterTransforms() {
        double sx = getStyled(SCALE_X);
        double sy = getStyled(SCALE_Y);
        double r = getStyled(ROTATE);
        double tx = getStyled(TRANSLATE_X);
        double ty = getStyled(TRANSLATE_Y);
        return sx != 1 || sy != 1 || r != 0 || tx != 0 || ty != 0;
    }

    default boolean hasTransforms() {
        return !get(TRANSFORMS).isEmpty();
    }

    @Override
    default boolean invalidateTransforms() {
        if (!CACHE) {
            return false;
        }
        // intentional use ofCollection long-circuit or-expressions!!
        return TransformCacheableFigure.super.invalidateTransforms()
                | null != setCachedValue(FigureImplementationDetails.PARENT_TO_LOCAL, null)
                | null != setCachedValue(FigureImplementationDetails.LOCAL_TO_PARENT, null);
    }

    @Override
    default void reshapeInLocal( Transform transform) {
        if (hasCenterTransforms() && !(transform instanceof Translate)) {
            ImmutableList<Transform> ts = get(TRANSFORMS);
            if (ts.isEmpty()) {
                set(TRANSFORMS, ImmutableList.of(transform));
            } else {
                int last = ts.size() - 1;
                Transform concatenatedWithLast = Transforms.concat(ts.get(last), transform);
                if (concatenatedWithLast instanceof Affine) {
                    set(TRANSFORMS, ImmutableList.add(ts, transform));
                } else {
                    set(TRANSFORMS, ImmutableList.set(ts, last, concatenatedWithLast));
                }
            }
            return;
        }

        Bounds b = getBoundsInLocal();
        b = transform.transform(b);
        reshapeInLocal(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    /**
     * Throws unsupported operation exception.
     * <p>
     * If subclass overrides {@link #reshapeInLocal(javafx.scene.transform.Transform) } then the
     * implementation of this method is most likely as follows:
     * <pre>
     *         reshapeInLocal(Transforms.createReshapeTransform(getBoundsInLocal(), x, y, width, height));
     * </pre>
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width
     * @param height the height
     */
    @Override
    default void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        throw new UnsupportedOperationException("this method must be overriden in class " + getClass());
    }

    @Override
    default void reshapeInParent( Transform transform) {
        final boolean hasCenters = hasCenterTransforms();
        final boolean hasTransforms = hasTransforms();
        if (!hasTransforms && (transform instanceof Translate)) {
            reshapeInLocal(transform);
            return;
        }
        if (hasCenters || hasTransforms()) {
            if (transform instanceof Translate) {
                Translate translate = (Translate) transform;
                if (!hasCenters) {
                    Point2D p = getParentToLocal().deltaTransform(translate.getTx(), translate.getTy());
                    reshapeInLocal(new Translate(p.getX(), p.getY()));
                } else {
                    set(TRANSLATE_X, get(TRANSLATE_X) + translate.getTx());
                    set(TRANSLATE_Y, get(TRANSLATE_Y) + translate.getTy());
                }
            } else {
                flattenTransforms();
                ImmutableList<Transform> transforms = get(TRANSFORMS);
                if (transforms.isEmpty()) {
                    set(TRANSFORMS, ImmutableList.of(transform));
                } else {
                    set(TRANSFORMS, ImmutableList.add(transforms, 0, transform));
                }
            }
        } else {
            reshapeInLocal(Transforms.concat(getParentToLocal(), transform));
        }
    }

    @Override
    default void transformInLocal( Transform t) {
        flattenTransforms();
        ImmutableList<Transform> transforms = get(TRANSFORMS);
        if (transforms.isEmpty()) {
            set(TRANSFORMS, ImmutableList.of(t));
        } else {
            set(TRANSFORMS, ImmutableList.add(transforms, t));
        }
    }

    @Override
    default void transformInParent(@javax.annotation.Nullable Transform t) {
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
            ImmutableList<Transform> transforms = get(TRANSFORMS);
            if (transforms.isEmpty()) {
                set(TRANSFORMS, ImmutableList.of(t));
            } else {
                set(TRANSFORMS, ImmutableList.add(transforms, 0, t));
            }
        }
    }

        @Nonnull
        public static Set<Key<?>> getDeclaredKeys() {
        Set<Key<?>> keys = new LinkedHashSet<>();
        Figure.getDeclaredKeys(TransformableFigure.class, keys);
        return keys;
    }
}
