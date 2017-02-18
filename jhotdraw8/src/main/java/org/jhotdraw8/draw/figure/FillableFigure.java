/* @(#)StrokedShapeFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.util.Objects;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import org.jhotdraw8.text.CssColor;
import org.jhotdraw8.text.Paintable;

/**
 * Interface figures which render a {@code javafx.scene.shape.Shape} and can be
 * filled.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface FillableFigure extends Figure {

    /**
     * Defines the paint used for filling the interior of the figure. Default
     * value: {@code Color.WHITE}.
     */
    public static PaintableStyleableFigureKey FILL = new PaintableStyleableFigureKey("fill", new CssColor("white", Color.WHITE));

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    default void applyFillableFigureProperties(Shape shape) {
        Paint p = Paintable.getPaint(getStyled(FILL));
        if (!Objects.equals(shape.getFill(), p)) {
            shape.setFill(p);
        }
    }

}
