/*
 * @(#)TextHolderFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import static org.jhotdraw.draw.TextHolderFigure.FONT;
import static org.jhotdraw.draw.TextHolderFigure.LINE_SPACING;
import static org.jhotdraw.draw.TextHolderFigure.STRIKETHROUGH;
import static org.jhotdraw.draw.TextHolderFigure.TEXT_ALIGNMENT;
import static org.jhotdraw.draw.TextHolderFigure.TEXT_ORIGIN;
import static org.jhotdraw.draw.TextHolderFigure.UNDERLINE;
import static org.jhotdraw.draw.TextHolderFigure.WRAPPING_WIDTH;
import org.jhotdraw.draw.key.PaintStyleableFigureKey;

/**
 * TextHolderFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface LabelFigure extends TextHolderFigure {

    /**
     * Defines the paint used for filling the interior of the text. Default
     * value: {@code Color.BLACK}.
     */
    public static PaintStyleableFigureKey TEXT_FILL = new PaintStyleableFigureKey("textFill", Color.BLACK);

    /**
     * Updates a text node with label properties.
     *
     * @param text a text node
     */
    default void applyLabelProperties(Text text) {
        text.setFill(getStyled(TEXT_FILL));
    }
}
