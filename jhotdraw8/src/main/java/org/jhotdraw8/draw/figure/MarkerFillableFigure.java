/* @(#)MarkerFillableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.util.Objects;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javax.annotation.Nonnull;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.NullableEnumStyleableFigureKey;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.key.NullablePaintableStyleableFigureKey;

/**
 * Interface figures which render a {@code javafx.scene.shape.Shape} and can be
 * filled.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface MarkerFillableFigure extends Figure {

    /**
     * Defines the paint used for filling the interior of the figure.
     * <p>
     * Default value: {@code Color.WHITE}.
     */
    public static NullablePaintableStyleableFigureKey MARKER_FILL = new NullablePaintableStyleableFigureKey("marker-fill", new CssColor("white", Color.WHITE));
    /**
     * Defines the fill-rule used for filling the interior of the figure..
     * <p>
     * Default value: {@code StrokeType.NON_ZERO}.
     */
    public static NullableEnumStyleableFigureKey<FillRule> MARKER_FILL_RULE = new NullableEnumStyleableFigureKey<>("marker-fill-rule", FillRule.class, DirtyMask.of(DirtyBits.NODE), false,FillRule.NON_ZERO);

    /**
     * Updates a shape node.
     *
     * @param shape a shape node
     */
    default void applyMarkerFillableFigureProperties(@Nonnull Shape shape) {
        Paint p = Paintable.getPaint(getStyled(MARKER_FILL));
        if (!Objects.equals(shape.getFill(), p)) {
            shape.setFill(p);
        }
        if (shape instanceof Path) {
            ((Path)shape).setFillRule(getStyled(MARKER_FILL_RULE));
        }
    }

}
