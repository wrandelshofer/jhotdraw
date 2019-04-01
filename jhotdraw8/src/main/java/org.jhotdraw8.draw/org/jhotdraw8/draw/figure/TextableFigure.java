/* @(#)TextableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.control.Labeled;
import javafx.scene.text.Text;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.StringStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * A figure which holds text in an attribute.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern Figure Mixin, Traits.
 */
public interface TextableFigure extends Figure {

    /**
     * The text. Default value: {@code ""}.
     */
    StringStyleableKey TEXT = new StringStyleableKey("text", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), "");

    /**
     * Updates a text node with textable properties.
     *
     * @param ctx  the render context
     * @param text a text node
     */
    default void applyTextableFigureProperties(@Nullable RenderContext ctx, @Nonnull Text text) {
        text.setText(getStyled(TEXT));
    }

    /**
     * Updates a text node with fontable properties.
     *
     * @param text a text node
     */
    default void applyTextableFigureProperties(@Nonnull Labeled text) {
        text.setText(getStyled(TEXT));
    }
}
