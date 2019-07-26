/*
 * @(#)TransformCacheableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.geom.Transforms;

import static org.jhotdraw8.draw.figure.FigureImplementationDetails.CACHE;
import static org.jhotdraw8.draw.figure.FigureImplementationDetails.IDENTITY_TRANSFORM;
import static org.jhotdraw8.draw.figure.FigureImplementationDetails.LOCAL_TO_WORLD;
import static org.jhotdraw8.draw.figure.FigureImplementationDetails.PARENT_TO_WORLD;
import static org.jhotdraw8.draw.figure.FigureImplementationDetails.WORLD_TO_LOCAL;
import static org.jhotdraw8.draw.figure.FigureImplementationDetails.WORLD_TO_PARENT;

/**
 * TransformCachingFigure.
 * <p>
 * This implementation is somewhat inefficient because we store the cached
 * values in a map.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern Figure Mixin, Traits.
 */
public interface TransformCacheableFigure extends CacheableFigure {

    @Override
    @Nullable
    default Transform getParentToWorld() {
        Transform t = CACHE ? getCachedValue(PARENT_TO_WORLD) : null;
        if (t == null) {
            t = getParent() == null ? IDENTITY_TRANSFORM : getParent().getLocalToWorld();
            if (CACHE) {
                setCachedValue(PARENT_TO_WORLD, t == null ? IDENTITY_TRANSFORM : t);
            }
        }
        return t == IDENTITY_TRANSFORM ? null : t;
    }

    @Override
    @Nullable
    default Transform getLocalToWorld() {
        Transform t = CACHE ? getCachedValue(LOCAL_TO_WORLD) : null;
        if (t == null) {
            t = getLocalToParent();
            final Figure parent = getParent();
            t = parent == null ? t : Transforms.concat(parent.getLocalToWorld(), t);
            if (CACHE) {
                setCachedValue(LOCAL_TO_WORLD, t == null ? IDENTITY_TRANSFORM : t);
            }
        }
        return t == IDENTITY_TRANSFORM ? null : t;
    }

    @Override
    @Nullable
    default Transform getWorldToLocal() {
        Transform t = getCachedValue(WORLD_TO_LOCAL);
        if (t == null) {
            t = getParentToLocal();
            final Figure parent = getParent();
            t = parent == null ? t : Transforms.concat(t, parent.getWorldToLocal());
            setCachedValue(WORLD_TO_LOCAL, t == null ? IDENTITY_TRANSFORM : t);
        }
        return t == IDENTITY_TRANSFORM ? null : t;
    }

    @Override
    @Nullable
    default Transform getWorldToParent() {
        Transform t = CACHE ? getCachedValue(WORLD_TO_PARENT) : null;
        if (t == null) {
            final Figure parent = getParent();
            t = parent == null ? IDENTITY_TRANSFORM : parent.getWorldToLocal();
            if (CACHE) {
                setCachedValue(WORLD_TO_PARENT, t == null ? IDENTITY_TRANSFORM : t);
            }
        }
        return t == IDENTITY_TRANSFORM ? null : t;
    }

    @Override
    default boolean invalidateTransforms() {
        if (!CACHE) {
            return false;
        }

        // intentional use of long-circuit or-expressions!!
        return null != setCachedValue(PARENT_TO_WORLD, null)
                | null != setCachedValue(LOCAL_TO_WORLD, null)
                | null != setCachedValue(WORLD_TO_LOCAL, null)
                | null != setCachedValue(WORLD_TO_PARENT, null);
    }
}
