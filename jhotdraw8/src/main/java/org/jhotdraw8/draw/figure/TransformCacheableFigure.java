/* @(#)TransformCachingFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.Key;
import static org.jhotdraw8.draw.figure.FigureImplementationDetails.*;
import org.jhotdraw8.geom.Transforms;

/**
 * TransformCachingFigure.
 * <p>
 * This implementation is somewhat inefficient because we store the cached
 * values in a map.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface TransformCacheableFigure extends CacheableFigure {

    @Override
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
    default Transform getLocalToWorld() {
        Transform t = CACHE ? getCachedValue(LOCAL_TO_WORLD) : null;
        if (t == null) {
            t = getLocalToParent();
            t = getParent() == null ? t : Transforms.concat(getParent().getLocalToWorld(), t);
            if (CACHE) {
                setCachedValue(LOCAL_TO_WORLD, t == null ? IDENTITY_TRANSFORM : t);
            }
        }
        return t == IDENTITY_TRANSFORM ? null : t;
    }

    @Override
    default Transform getWorldToLocal() {
        Transform t = getCachedValue(WORLD_TO_LOCAL);
        if (t == null) {
            t = getParentToLocal();
            t = getParent() == null ? t : Transforms.concat(t, getParent().getWorldToLocal());
            setCachedValue(WORLD_TO_LOCAL, t == null ? IDENTITY_TRANSFORM : t);
        }
        return t == IDENTITY_TRANSFORM ? null : t;
    }

    @Override
    default Transform getWorldToParent() {
        Transform t = CACHE ? getCachedValue(WORLD_TO_PARENT) : null;
        if (t == null) {
            t = getParent() == null ? IDENTITY_TRANSFORM : getParent().getWorldToLocal();
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
