/*
 * @(#)SimpleLayeredDrawing.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.render.RenderContext;

public class SimpleLayeredDrawing extends AbstractDrawing
        implements LayeredDrawing, StyleableFigure, LockableFigure, NonTransformableFigure {
    public SimpleLayeredDrawing() {
    }

    public SimpleLayeredDrawing(double width, double height) {
        super(width, height);
    }

    public SimpleLayeredDrawing(CssSize width, CssSize height) {
        super(width, height);
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node n) {
        super.updateNode(ctx, n);
        applyStyleableFigureProperties(ctx, n);
    }
}
