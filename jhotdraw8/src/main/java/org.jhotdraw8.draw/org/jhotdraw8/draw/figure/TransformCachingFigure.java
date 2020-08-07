/*
 * @(#)TransformCacheableFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.geom.FXTransforms;

import static org.jhotdraw8.draw.figure.FigureImplementationDetails.CACHE;
import static org.jhotdraw8.draw.figure.FigureImplementationDetails.IDENTITY_TRANSFORM;

/**
 * TransformCachingFigure.
 * <p>
 * This implementation is somewhat inefficient because we store the cached
 * values in a map.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 */
public interface TransformCachingFigure extends Figure {

    @Nullable
    Transform getCachedLocalToWorld();

    void setCachedLocalToWorld(@Nullable Transform newValue);

    @Nullable
    Transform getCachedParentToWorld();

    void setCachedParentToWorld(@Nullable Transform newValue);


    @Nullable
    Transform getCachedWorldToLocal();

    void setCachedWorldToLocal(@Nullable Transform newValue);


    @Nullable
    Transform getCachedWorldToParent();

    void setCachedWorldToParent(@Nullable Transform newValue);

    @Override
    @Nullable
    default Transform getParentToWorld() {
        Transform t = CACHE ? getCachedParentToWorld() : null;
        if (t == null) {
            t = getParent() == null ? IDENTITY_TRANSFORM : getParent().getLocalToWorld();
            if (CACHE) {
                setCachedParentToWorld(t == null ? IDENTITY_TRANSFORM : t);
            }
        }
        return t == IDENTITY_TRANSFORM ? null : t;
    }


    @Override
    @Nullable
    default Transform getLocalToWorld() {
        Transform t = CACHE ? getCachedLocalToWorld() : null;
        if (t == null) {
            t = getLocalToParent();
            final Figure parent = getParent();
            t = parent == null ? t : FXTransforms.concat(parent.getLocalToWorld(), t);
            if (CACHE) {
                setCachedLocalToWorld(t == null ? IDENTITY_TRANSFORM : t);
            }
        }
        return t == IDENTITY_TRANSFORM ? null : t;
    }

    @Override
    @Nullable
    default Transform getWorldToLocal() {
        Transform t = getCachedWorldToLocal();
        if (t == null) {
            t = getParentToLocal();
            final Figure parent = getParent();
            t = parent == null ? t : FXTransforms.concat(t, parent.getWorldToLocal());
            setCachedWorldToLocal(t == null ? IDENTITY_TRANSFORM : t);
        }
        return t == IDENTITY_TRANSFORM ? null : t;
    }

    @Override
    @Nullable
    default Transform getWorldToParent() {
        Transform t = CACHE ? getCachedWorldToParent() : null;
        if (t == null) {
            final Figure parent = getParent();
            t = parent == null ? IDENTITY_TRANSFORM : parent.getWorldToLocal();
            if (CACHE) {
                setCachedWorldToParent(t == null ? IDENTITY_TRANSFORM : t);
            }
        }
        return t == IDENTITY_TRANSFORM ? null : t;
    }

    @Nullable
    Transform getCachedLocalToParent();

    void setCachedLocalToParent(@Nullable Transform newValue);

    @Nullable
    Transform getCachedParentToLocal();

    void setCachedParentToLocal(@Nullable Transform newValue);

    @Override
    default void invalidateTransforms() {
        setCachedWorldToLocal(null);
        setCachedWorldToParent(null);
        setCachedLocalToWorld(null);
        setCachedParentToWorld(null);
        setCachedParentToLocal(null);
        setCachedLocalToParent(null);
    }
}
