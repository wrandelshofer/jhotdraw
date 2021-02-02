/*
 * @(#)TextableFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.control.Labeled;
import javafx.scene.text.Text;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.key.NullableStringStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * A figure which holds text in an attribute.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 */
public interface TextableFigure extends Figure {

    /**
     * The text. Default value: {@code ""}.
     */
    NullableStringStyleableKey TEXT = new NullableStringStyleableKey("text");

    /**
     * Updates a text node with textable properties.
     * <p>
     * You must set the font before you set the text, so that JavaFx does not need to retrieve
     * the system default font, which on Windows requires that the JavaFx Toolkit is launched.
     *
     * @param ctx  the render context
     * @param text a text node
     */
    default void applyTextableFigureProperties(@Nullable RenderContext ctx, @NonNull Text text) {
        text.setText(getStyled(TEXT));
    }

    /**
     * Updates a text node with fontable properties.
     *
     * @param text a text node
     */
    default void applyTextableFigureProperties(@NonNull Labeled text) {
        text.setText(getStyled(TEXT));
    }
}
