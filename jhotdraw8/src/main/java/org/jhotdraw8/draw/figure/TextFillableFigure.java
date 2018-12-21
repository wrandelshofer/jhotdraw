/* @(#)TextableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javax.annotation.Nonnull;
import org.jhotdraw8.draw.key.NullablePaintableStyleableFigureKey;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * {@code TextFillableFigure} allows to change the fill of the text.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface TextFillableFigure extends Figure {

    /**
     * Defines the paint used for filling the interior of the text. Default
     * value: {@code Color.BLACK}.
     */
    public static NullablePaintableStyleableFigureKey TEXT_FILL = new NullablePaintableStyleableFigureKey("textFill", new CssColor("black", Color.BLACK));

    /**
     * Updates a text node with label properties.
     *  @param ctx
     * @param text a text node
     */
    default void applyTextFillableFigureProperties(@Nonnull RenderContext ctx, @Nonnull Text text) {
        text.setFill(Paintable.getPaint(getStyled(TEXT_FILL)));
    }

    /**
     * Updates a text node with label properties.
     *
     * @param text a text node
     */
    default void applyTextFillableFigureProperties(@Nonnull RenderContext ctx, @Nonnull Labeled text) {
        text.setTextFill(Paintable.getPaint(getStyled(TEXT_FILL)));
    }
}
