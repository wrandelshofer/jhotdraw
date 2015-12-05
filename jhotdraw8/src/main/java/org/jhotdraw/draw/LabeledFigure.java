/*
 * @(#)TextableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.geometry.VPos;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jhotdraw.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.EnumStyleableFigureKey;
import org.jhotdraw.draw.key.FontStyleableMapAccessor;
import org.jhotdraw.draw.key.PaintStyleableFigureKey;
import org.jhotdraw.draw.key.StringOrIdentStyleableFigureKey;

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
