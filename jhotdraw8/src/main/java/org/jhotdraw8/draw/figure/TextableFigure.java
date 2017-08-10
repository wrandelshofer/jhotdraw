/* @(#)TextableFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.control.Labeled;
import javafx.scene.text.Text;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.StringStyleableFigureKey;

/**
 * A figure which holds text in an attribute.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface TextableFigure extends Figure {

    /**
     * The text. Default value: {@code ""}.
     */
    public final static StringStyleableFigureKey TEXT = new StringStyleableFigureKey("text", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), "");

    /**
     * Updates a text node with textable properties.
     *
     * @param text a text node
     */
    default void applyTextableFigureProperties(Text text) {
        text.setText(getStyled(TEXT));
    }

    /**
     * Updates a text node with fontable properties.
     *
     * @param text a text node
     */
    default void applyTextableFigureProperties(Labeled text) {
        text.setText(getStyled(TEXT));
    }
}
