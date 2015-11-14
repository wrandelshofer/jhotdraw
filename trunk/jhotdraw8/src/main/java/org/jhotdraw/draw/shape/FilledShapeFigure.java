/* @(#)StrokedShapeFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import java.util.Collections;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.AbstractLeafFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.SimpleFigureKey;
import org.jhotdraw.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw.draw.key.DoubleListStyleableFigureKey;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.EnumStyleableFigureKey;
import org.jhotdraw.draw.key.PaintStyleableFigureKey;

/**
 * Interface figures which render a {@code javafx.scene.shape.Shape} and
 * can be filled.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FilledShapeFigure extends Figure {

    /**
     * Defines the paint used for filling the interior of the figure. Default
     * value: {@code Color.WHITE}.
     */
    public static PaintStyleableFigureKey FILL_COLOR = new PaintStyleableFigureKey("fill", Color.WHITE);
    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    default void applyFilledShapeProperties(Shape shape) {
        shape.setFill(getStyled(FILL_COLOR));
    }



}
