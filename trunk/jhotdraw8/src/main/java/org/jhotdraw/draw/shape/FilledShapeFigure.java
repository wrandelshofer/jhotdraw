/* @(#)StrokedShapeFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import org.jhotdraw.draw.TransformableFigure;
import org.jhotdraw.draw.key.PaintStyleableFigureKey;

/**
 * Interface figures which render a {@code javafx.scene.shape.Shape} and
 * can be filled.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FilledShapeFigure extends TransformableFigure {

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
