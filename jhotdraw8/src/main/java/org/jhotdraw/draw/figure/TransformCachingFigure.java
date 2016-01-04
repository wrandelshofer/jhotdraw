/* @(#)TransformCachingFigure.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.figure;

import javafx.scene.transform.Transform;

/**
 * TransformCachingFigure.
 * <p>
 * This implementation is somewhat inefficient because we store the cached
 * values in a map.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface TransformCachingFigure extends Figure {
    @Override
    default Transform getParentToWorld() {
        Transform t = get(FigureImplementationDetails.PARENT_TO_WORLD);
        if (t == null) {
            t = getParent() == null ? FigureImplementationDetails.IDENTITY_TRANSFORM : getParent().getLocalToWorld();
            set(FigureImplementationDetails.PARENT_TO_WORLD, t);
        }
        return t;
    }

    @Override
    default Transform getLocalToWorld() {
        Transform t = get(FigureImplementationDetails.LOCAL_TO_WORLD);
        if (t == null) {
            t = getLocalToParent();
            t = getParent() == null ? t : getParent().getLocalToWorld().createConcatenation(t);
            set(FigureImplementationDetails.LOCAL_TO_WORLD, t);
        }
        return t;
    }

    @Override
    default Transform getWorldToLocal() {
        Transform t = get(FigureImplementationDetails.WORLD_TO_LOCAL);
        if (t == null) {
            t = getParentToLocal();
            t = getParent() == null ? t : t.createConcatenation(getParent().getWorldToLocal());
            set(FigureImplementationDetails.WORLD_TO_LOCAL, t);
        }
        return t;
    }

    @Override
    default Transform getWorldToParent() {
        Transform t = get(FigureImplementationDetails.WORLD_TO_PARENT);
        if (t == null) {
            t = getParent() == null ? FigureImplementationDetails.IDENTITY_TRANSFORM : getParent().getWorldToLocal();
            set(FigureImplementationDetails.WORLD_TO_PARENT, t);
        }
        return t;
    }

    @Override
    default void invalidateTransforms() {
        set(FigureImplementationDetails.PARENT_TO_WORLD, null);
        set(FigureImplementationDetails.LOCAL_TO_WORLD, null);
        set(FigureImplementationDetails.WORLD_TO_LOCAL, null);
        set(FigureImplementationDetails.WORLD_TO_PARENT, null);
    }

}
