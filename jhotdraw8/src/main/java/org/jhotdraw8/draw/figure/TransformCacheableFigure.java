/* @(#)TransformCachingFigure.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.transform.Transform;
import static org.jhotdraw8.draw.figure.FigureImplementationDetails.*;

/**
 * TransformCachingFigure.
 * <p>
 * This implementation is somewhat inefficient because we store the cached
 * values in a map.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id: TransformCacheableFigure.java 1120 2016-01-15 17:37:49Z
 * rawcoder $
 */
public interface TransformCacheableFigure extends Figure {

    @Override
    default Transform getParentToWorld() {
        Transform t = CACHE ? get(PARENT_TO_WORLD) : null;
        if (t == null) {
            t = getParent() == null ? IDENTITY_TRANSFORM : getParent().getLocalToWorld();
            if (CACHE) {
                set(PARENT_TO_WORLD, t);
            }
        }
        return t;
    }

    @Override
    default Transform getLocalToWorld() {
        Transform t = CACHE ? get(LOCAL_TO_WORLD) : null;
        if (t == null) {
            t = getLocalToParent();
            t = getParent() == null ? t : getParent().getLocalToWorld().createConcatenation(t);
            if (CACHE) {
                set(LOCAL_TO_WORLD, t);
            }
        }
        return t;
    }

    @Override
    default Transform getWorldToLocal() {
        Transform t = get(WORLD_TO_LOCAL);
        if (true || t == null) {
            t = getParentToLocal();
            t = getParent() == null ? t : t.createConcatenation(getParent().getWorldToLocal());
            set(WORLD_TO_LOCAL, t);
        }
        return t;
    }

    @Override
    default Transform getWorldToParent() {
        Transform t = CACHE ? get(WORLD_TO_PARENT) : null;
        if (t == null) {
            t = getParent() == null ? IDENTITY_TRANSFORM : getParent().getWorldToLocal();
            if (CACHE) {
                set(WORLD_TO_PARENT, t);
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
        return null != set(PARENT_TO_WORLD, null)
                | null != set(LOCAL_TO_WORLD, null)
                | null != set(WORLD_TO_LOCAL, null)
                | null != set(WORLD_TO_PARENT, null);
    }

}
