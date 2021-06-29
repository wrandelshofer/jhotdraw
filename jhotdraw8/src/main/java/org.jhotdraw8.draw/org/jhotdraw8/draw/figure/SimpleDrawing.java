/*
 * @(#)SimpleDrawing.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * A simple implementation of {@link Drawing}.
 */
public class SimpleDrawing extends AbstractDrawing
        implements StyleableFigure, LockableFigure {
    public SimpleDrawing(double width, double height) {
        super(width, height);
    }

    public SimpleDrawing() {
    }

    @Override
    public boolean isSuitableChild(@NonNull Figure newChild) {
        return true;
    }

    @Override
    public void reshapeInParent(@NonNull Transform transform) {
        // cannot be reshaped
    }

    @Override
    public void transformInLocal(@NonNull Transform transform) {
        // cannot be transformed
    }

    @Override
    public void transformInParent(@NonNull Transform transform) {
        // cannot be transformed
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node n) {
        super.updateNode(ctx, n);
        applyStyleableFigureProperties(ctx, n);
    }
}
