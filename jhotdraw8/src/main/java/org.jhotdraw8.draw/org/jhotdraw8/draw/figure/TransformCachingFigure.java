/*
 * @(#)TransformCacheableFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.geom.FXTransforms;


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
    boolean CACHE = true;
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
    default @NonNull Transform getParentToWorld() {
        Transform t = CACHE ? getCachedParentToWorld() : null;
        if (t == null) {
            t = getParent() == null ? FXTransforms.IDENTITY : getParent().getLocalToWorld();
            if (CACHE) {
                setCachedParentToWorld(t);
            }
        }
        return t;
    }


    @Override
    default @NonNull Transform getLocalToWorld() {
        Transform t = CACHE ? getCachedLocalToWorld() : null;
        if (t == null) {
            t = getLocalToParent();
            final Figure parent = getParent();
            t = parent == null ? t : FXTransforms.concat(parent.getLocalToWorld(), t);
            if (CACHE) {
                setCachedLocalToWorld(t);
            }
        }
        return t;
    }

    @Override
    default @NonNull Transform getWorldToLocal() {
        Transform t = getCachedWorldToLocal();
        if (t == null) {
            t = getParentToLocal();
            final Figure parent = getParent();
            t = parent == null ? t : FXTransforms.concat(t, parent.getWorldToLocal());
            setCachedWorldToLocal(t);
        }
        return t;
    }

    @Override
    default @NonNull Transform getWorldToParent() {
        Transform t = CACHE ? getCachedWorldToParent() : null;
        if (t == null) {
            final Figure parent = getParent();
            t = parent == null ? FXTransforms.IDENTITY : parent.getWorldToLocal();
            if (CACHE) {
                setCachedWorldToParent(t);
            }
        }
        return t;
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
