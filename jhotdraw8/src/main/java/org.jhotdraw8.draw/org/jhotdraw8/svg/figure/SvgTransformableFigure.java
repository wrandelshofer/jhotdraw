/*
 * @(#)SvgTransformableFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.TransformCachingFigure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.draw.key.TransformListStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.FXTransforms;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * A transformable figure supports the transformation of a figure.
 * <p>
 * The following transformations are supported:
 * <ul>
 * <li>Arbitrary sequence of affine transformations of the
 * figure.</li>
 * </ul>
 * Note that transformation matrices computed from the Rotation and Scaling must
 * be recomputed every time when the local bounds of the figure
 * change.
 *
 * @author Werner Randelshofer
 */
public interface SvgTransformableFigure extends TransformCachingFigure {
    boolean CACHE = true;
    @NonNull
    TransformListStyleableKey TRANSFORMS = TransformableFigure.TRANSFORMS;

    static @NonNull Set<Key<?>> getDeclaredKeys() {
        Set<Key<?>> keys = new LinkedHashSet<>();
        Figure.getDeclaredKeys(SvgTransformableFigure.class, keys);
        return keys;
    }

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
     * @param ctx  the render context
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applyTransformableFigureProperties(@NonNull RenderContext ctx, @NonNull Node node) {
        Transform t = getLocalToParent();
        List<Transform> transforms = node.getTransforms();
        if (t == null || t.isIdentity()) {
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
        remove(TRANSFORMS);
    }

    default void flattenTransforms() {
        Transform p2l = getLocalToParent(false);
        if (p2l == null || p2l.isIdentity()) {
            remove(TRANSFORMS);
        } else {
            set(TRANSFORMS, ImmutableLists.of(p2l));
        }
    }


    default @Nullable Transform getInverseTransform() {
        ImmutableList<Transform> list = getStyledNonNull(TRANSFORMS);
        Transform t;
        if (list.isEmpty()) {
            t = null; // leave null
        } else {
            try {
                t = list.get(list.size() - 1).createInverse();
                for (int i = list.size() - 2; i >= 0; i--) {
                    t = FXTransforms.concat(t, list.get(i).createInverse());
                }
            } catch (NonInvertibleTransformException e) {
                throw new InternalError(e);
            }
        }
        return t;
    }

    @Override
    default @NonNull Transform getLocalToParent() {
        return getLocalToParent(true);
    }

    default @NonNull Transform getLocalToParent(boolean styled) {
        Transform l2p = CACHE && styled ? getCachedLocalToParent() : null;
        if (l2p == null) {
            Point2D center = getCenterInLocal();

            ImmutableList<Transform> t = styled ? getStyled(TRANSFORMS) : get(TRANSFORMS);
            if (t != null && !t.isEmpty()) {
                l2p = FXTransforms.concat(l2p, getTransform());
            }
            if (l2p == null) {
                l2p = FXTransforms.IDENTITY;
            }
            if (CACHE && styled) {
                setCachedLocalToParent(l2p);
            }
        }
        return l2p;
    }

    default @NonNull List<Transform> getLocalToParentAsList(boolean styled) {
        ArrayList<Transform> list = new ArrayList<>();

        Point2D center = getCenterInLocal();

        ImmutableList<Transform> t = styled ? getStyledNonNull(TRANSFORMS) : getNonNull(TRANSFORMS);
        if (!t.isEmpty()) {
            list.addAll(t.asList());
        }
        return list;
    }

    default @NonNull Transform getParentToLocal() {
        return getParentToLocal(true);
    }

    default @NonNull Transform getParentToLocal(boolean styled) {
        Transform p2l = CACHE ? getCachedParentToLocal() : null;
        if (p2l == null) {
            Point2D center = getCenterInLocal();

            ImmutableList<Transform> t = styled ? getStyled(TRANSFORMS) : get(TRANSFORMS);

            if (t != null && !t.isEmpty()) {
                p2l = getInverseTransform();
            }
            if (CACHE) {
                setCachedParentToLocal(p2l);
            }
        }
        return p2l;
    }

    default @Nullable Transform getTransform() {
        ImmutableList<Transform> list = getStyledNonNull(TRANSFORMS);
        Transform t;
        if (list.isEmpty()) {
            t = null; // leave empty
        } else {
            t = list.get(0);
            for (int i = 1, n = list.size(); i < n; i++) {
                t = FXTransforms.concat(t, list.get(i));
            }
        }
        return t;
    }

    default boolean hasCenterTransforms() {
        return false;
    }

    default boolean hasTransforms() {
        return !getNonNull(TRANSFORMS).isEmpty();
    }

    @Override
    default void reshapeInLocal(Transform transform) {
        if (hasCenterTransforms() && !(transform instanceof Translate)) {
            ImmutableList<Transform> ts = getNonNull(TRANSFORMS);
            if (ts.isEmpty()) {
                set(TRANSFORMS, ImmutableLists.of(transform));
            } else {
                int last = ts.size() - 1;
                Transform concatenatedWithLast = FXTransforms.concat(ts.get(last), transform);
                if (concatenatedWithLast instanceof Affine) {
                    set(TRANSFORMS, ImmutableLists.add(ts, transform));
                } else {
                    set(TRANSFORMS, ImmutableLists.set(ts, last, concatenatedWithLast));
                }
            }
            return;
        }

        Bounds b = getLayoutBounds();
        b = transform.transform(b);
        reshapeInLocal(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    default void reshapeInParent(@NonNull Transform transform) {
        final boolean hasCenters = hasCenterTransforms();
        final boolean hasTransforms = hasTransforms();
        if (!hasTransforms && (transform instanceof Translate)) {
            reshapeInLocal(transform);
            return;
        }
        Transform parentToLocal = getParentToLocal();
        if (hasCenters || hasTransforms()) {
            if (transform instanceof Translate) {
                Translate translate = (Translate) transform;
                if (!hasCenters) {
                    Point2D p = parentToLocal == null ? new Point2D(translate.getTx(), translate.getTy())
                            : parentToLocal.deltaTransform(translate.getTx(), translate.getTy());
                    reshapeInLocal(new Translate(p.getX(), p.getY()));
                } else {
                    ImmutableList<Transform> transforms = getNonNull(TRANSFORMS);
                    Transform lastTransform = transforms.get(transforms.size() - 1);
                    if (lastTransform instanceof Translate) {
                        set(TRANSFORMS, ImmutableLists.set(transforms, transforms.size() - 1,
                                lastTransform.createConcatenation(translate)));
                    } else {
                        set(TRANSFORMS, ImmutableLists.add(transforms, 0, translate));
                    }
                }
            } else {
                flattenTransforms();
                ImmutableList<Transform> transforms = getNonNull(TRANSFORMS);
                if (transforms.isEmpty()) {
                    set(TRANSFORMS, ImmutableLists.of(transform));
                } else {
                    set(TRANSFORMS, ImmutableLists.add(transforms, 0, transform));
                }
            }
        } else {
            reshapeInLocal(FXTransforms.concat(parentToLocal, transform));
        }
    }

    /**
     * Convenience method for setting a new value for the {@link #TRANSFORMS}
     * property.
     *
     * @param transforms new value
     */
    default void setTransforms(@NonNull Transform... transforms) {
        if (transforms.length == 1 && transforms[0].isIdentity()) {
            set(TRANSFORMS, ImmutableLists.emptyList());
        } else {
            set(TRANSFORMS, ImmutableLists.of(transforms));
        }
    }

    @Override
    default void transformInLocal(@NonNull Transform t) {
        flattenTransforms();
        ImmutableList<Transform> transforms = getNonNull(TRANSFORMS);
        if (transforms.isEmpty()) {
            set(TRANSFORMS, ImmutableLists.of(t));
        } else {
            set(TRANSFORMS, ImmutableLists.add(transforms, t));
        }
    }

    @Override
    default void transformInParent(@NonNull Transform t) {
        if (t.isIdentity()) {
            return;
        }
        if (t instanceof Translate) {
            Translate tr = (Translate) t;
            flattenTransforms();
            ImmutableList<Transform> transforms = getNonNull(TRANSFORMS);
            if (transforms.isEmpty()) {
                translateInLocal(new CssPoint2D(tr.getTx(), tr.getTy()));
            } else {
                Transform lastTransform = transforms.get(transforms.size() - 1);
                if (lastTransform instanceof Translate) {
                    set(TRANSFORMS, ImmutableLists.set(transforms, transforms.size() - 1,
                            lastTransform.createConcatenation(t)));
                } else {
                    set(TRANSFORMS, ImmutableLists.add(transforms, 0, t));
                }
            }
        } else {
            flattenTransforms();
            ImmutableList<Transform> transforms = getNonNull(TRANSFORMS);
            if (transforms.isEmpty()) {
                set(TRANSFORMS, ImmutableLists.of(t));
            } else {
                set(TRANSFORMS, ImmutableLists.add(transforms, 0, t));
            }
        }
    }
}
