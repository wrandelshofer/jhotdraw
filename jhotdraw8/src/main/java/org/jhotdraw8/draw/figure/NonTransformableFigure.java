/* @(#)NonTransformableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.transform.Transform;

/**
 * Provides default implementations for figures which can not be transformed.
 *
 * @design.pattern Figure Mixin, Traits.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface NonTransformableFigure extends TransformCacheableFigure {
    @Override
    default void transformInParent(Transform transform) {
        // empty because non-transformable figures can not be transformed
    }
    @Override
    default void reshapeInParent(Transform transform) {
        reshapeInLocal(getParentToLocal().createConcatenation(transform));
    }

    @Override
    default Transform getLocalToParent() {
        return FigureImplementationDetails.IDENTITY_TRANSFORM;
    }

    @Override
    default Transform getParentToLocal() {
        return FigureImplementationDetails.IDENTITY_TRANSFORM;
    }
}
