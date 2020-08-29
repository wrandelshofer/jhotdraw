/*
 * @(#)SvgDrawing.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.figure;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.text.CssRectangle2DConverter;
import org.jhotdraw8.draw.figure.AbstractDrawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.NonTransformableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.styleable.SimpleStyleableKey;

/**
 * Represents an SVG 'svg' element.
 */
public class SvgDrawing extends AbstractDrawing
        implements StyleableFigure, LockableFigure, NonTransformableFigure, SvgInheritableFigure,
        SvgElementFigure {

    public static final @NonNull Key<CssRectangle2D> VIEW_BOX = new SimpleStyleableKey<>("viewBox", CssRectangle2D.class, null, new CssRectangle2DConverter(true));

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
