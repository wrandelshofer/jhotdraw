/* @(#)FillableFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.util.Objects;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import org.jhotdraw8.draw.key.CssColor;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.draw.key.Paintable;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;

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
     * Defines the paint used for filling the interior of the figure.
     * <p>
     * Default value: {@code Color.WHITE}.
     */
    public static PaintableStyleableFigureKey FILL = new PaintableStyleableFigureKey("fill", new CssColor("white", Color.WHITE));
    /**
     * Defines the fill-rule used for filling the interior of the figure..
     * <p>
     * Default value: {@code StrokeType.NON_ZERO}.
     */
    public static EnumStyleableFigureKey<FillRule> FILL_RULE = new EnumStyleableFigureKey<>("fill-rule", FillRule.class, DirtyMask.of(DirtyBits.NODE), false,FillRule.NON_ZERO);

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    default void applyFillableFigureProperties( Shape shape) {
        Paint p = Paintable.getPaint(getStyled(FILL));
        if (!Objects.equals(shape.getFill(), p)) {
            shape.setFill(p);
        }
        if (shape instanceof Path) {
            ((Path)shape).setFillRule(getStyled(FILL_RULE));
        }
    }

}
