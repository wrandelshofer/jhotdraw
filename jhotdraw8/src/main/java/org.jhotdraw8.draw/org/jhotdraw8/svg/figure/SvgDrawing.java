/*
 * @(#)SvgDrawing.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.figure;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.text.CssRectangle2DConverter;
import org.jhotdraw8.css.text.CssStringConverter;
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
        implements StyleableFigure, LockableFigure, NonTransformableFigure, SvgDefaultableFigure,
        SvgElementFigure {

    public static final @NonNull Key<CssRectangle2D> VIEW_BOX = new SimpleStyleableKey<>("viewBox", "viewBox", CssRectangle2D.class, null, new CssRectangle2DConverter(true), null);
    public static final @NonNull Key<String> BASE_PROFILE = new SimpleStyleableKey<>("baseProfile", "baseProfile", String.class, null, new CssStringConverter(true), null);
    public static final @NonNull Key<String> VERSION = new SimpleStyleableKey<>("version", "version", String.class, null, new CssStringConverter(true), null);

    @Override
    public boolean isSuitableChild(@NonNull Figure newChild) {
        return true;
    }


    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node n) {
        super.updateNode(ctx, n);
        applyStyleableFigureProperties(ctx, n);
    }

    @Override
    public void updateBackground(RenderContext ctx, Pane g) {
        // background is always transparent!
    }

    @Override
    public @NonNull String getTypeSelector() {
        return "svg";
    }
}
