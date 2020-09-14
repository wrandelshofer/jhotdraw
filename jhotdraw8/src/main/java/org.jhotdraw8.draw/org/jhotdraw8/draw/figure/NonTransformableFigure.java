/*
 * @(#)NonTransformableFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Bounds;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.FXTransforms;

/**
 * Provides default implementations for figures which can not be transformed.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 */
public interface NonTransformableFigure extends TransformCachingFigure {

    @Override
    default void transformInParent(@NonNull Transform transform) {
        // empty because non-transformable figures can not be transformed
    }

    @Override
    default void transformInLocal(@NonNull Transform transform) {
        // empty because non-transformable figures can not be transformed
    }

    @Override
    default void reshapeInParent(@NonNull Transform transform) {
        reshapeInLocal(FXTransforms.concat(getParentToLocal(), transform));
    }

    @Override
    default @NonNull Transform getLocalToParent() {
        return FXTransforms.IDENTITY;
    }

    @Override
    default @NonNull Transform getParentToLocal() {
        return FXTransforms.IDENTITY;
    }

    @Override
    default void reshapeInLocal(@NonNull Transform transform) {
        Bounds b = getLayoutBounds();
        b = transform.transform(b);
        reshapeInLocal(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }
}
