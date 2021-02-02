/*
 * @(#)TextFillableFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.key.NullablePaintableStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * {@code TextFillableFigure} allows to change the fill of the text.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 */
public interface TextFillableFigure extends Figure {

    /**
     * Defines the paint used for filling the interior of the text. Default
     * value: {@code Color.BLACK}.
     */
    NullablePaintableStyleableKey TEXT_FILL = new NullablePaintableStyleableKey("textFill", new CssColor("canvastext", Color.BLACK));

    /**
     * Updates a text node with label properties.
     *
     * @param ctx  the render context
     * @param text a text node
     */
    default void applyTextFillableFigureProperties(@NonNull RenderContext ctx, @NonNull Text text) {
        text.setFill(Paintable.getPaint(getStyled(TEXT_FILL), ctx));
    }

    /**
     * Updates a text node with label properties.
     *
     * @param ctx  the render context
     * @param text a text node
     */
    default void applyTextFillableFigureProperties(@NonNull RenderContext ctx, @NonNull Labeled text) {
        text.setTextFill(Paintable.getPaint(getStyled(TEXT_FILL), ctx));
    }
}
