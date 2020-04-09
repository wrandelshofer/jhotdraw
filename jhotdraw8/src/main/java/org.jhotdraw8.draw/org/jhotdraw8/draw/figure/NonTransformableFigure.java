/*
 * @(#)NonTransformableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Bounds;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.geom.Transforms;

/**
 * Provides default implementations for figures which can not be transformed.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 */
public interface NonTransformableFigure extends TransformCacheableFigure {

    @Override
    default void transformInParent(Transform transform) {
        // empty because non-transformable figures can not be transformed
    }

    @Override
    default void transformInLocal(Transform transform) {
        // empty because non-transformable figures can not be transformed
    }

    @Override
    default void reshapeInParent(Transform transform) {
        reshapeInLocal(Transforms.concat(getParentToLocal(), transform));
    }

    @Override
    @Nullable
    default Transform getLocalToParent() {
        return FigureImplementationDetails.IDENTITY_TRANSFORM;
    }

    @Override
    @Nullable
    default Transform getParentToLocal() {
        return FigureImplementationDetails.IDENTITY_TRANSFORM;
    }

    @Override
    default void reshapeInLocal(@NonNull Transform transform) {
        Bounds b = getLayoutBounds();
        b = transform.transform(b);
        reshapeInLocal(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }
}
