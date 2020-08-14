/*
 * @(#)SvgDrawing.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.figure;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.figure.AbstractDrawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.NonTransformableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * Represents an SVG 'svg' element.
 */
public class SvgDrawing extends AbstractDrawing
        implements StyleableFigure, LockableFigure, NonTransformableFigure {
    @Override
    public boolean isSuitableChild(@NonNull Figure newChild) {
        return true;
    }


    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node n) {
        super.updateNode(ctx, n);
        applyStyleableFigureProperties(ctx, n);
    }
}
