/*
 * @(#)TextableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.jhotdraw.draw.key.PaintStyleableFigureKey;

/**
 * {@code LabeledFigure} is a {@code TextableFigure} which allows to change
 * the fill color of the text.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface LabeledFigure extends Figure {

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
    default void applyLabeledFigureProperties(Text text) {
        text.setFill(getStyled(TEXT_FILL));
    }
    /**
     * Updates a text node with label properties.
     *
     * @param text a text node
     */
    default void applyLabeledFigureProperties(Labeled text) {
        text.setTextFill(getStyled(TEXT_FILL));
    }
}
