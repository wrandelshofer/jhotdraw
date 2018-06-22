/* @(#)TextableFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import org.jhotdraw8.draw.key.CssColor;
import org.jhotdraw8.draw.key.Paintable;

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
    public static PaintableStyleableFigureKey TEXT_FILL = new PaintableStyleableFigureKey("textFill", new CssColor("black", Color.BLACK));

    /**
     * Updates a text node with label properties.
     *
     * @param text a text node
     */
    default void applyTextFillableFigureProperties(@NonNull Text text) {
        text.setFill(Paintable.getPaint(getStyled(TEXT_FILL)));
    }

    /**
     * Updates a text node with label properties.
     *
     * @param text a text node
     */
    default void applyTextFillableFigureProperties(@NonNull Labeled text) {
        text.setTextFill(Paintable.getPaint(getStyled(TEXT_FILL)));
    }
}
