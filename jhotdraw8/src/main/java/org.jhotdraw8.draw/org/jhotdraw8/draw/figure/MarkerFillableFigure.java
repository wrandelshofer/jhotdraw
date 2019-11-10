/*
 * @(#)MarkerFillableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.NullableEnumStyleableKey;
import org.jhotdraw8.draw.key.NullablePaintableStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;

import java.util.Objects;

/**
 * Interface figures which render a {@code javafx.scene.shape.Shape} and can be
 * filled.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 */
public interface MarkerFillableFigure extends Figure {

    /**
     * Defines the paint used for filling the interior of the figure.
     * <p>
     * Default value: {@code Color.WHITE}.
     */
    NullablePaintableStyleableKey MARKER_FILL = new NullablePaintableStyleableKey("marker-fill", new CssColor("white", Color.WHITE));
    /**
     * Defines the fill-rule used for filling the interior of the figure..
     * <p>
     * Default value: {@code StrokeType.NON_ZERO}.
     */
    NullableEnumStyleableKey<FillRule> MARKER_FILL_RULE = new NullableEnumStyleableKey<>("marker-fill-rule", FillRule.class, DirtyMask.of(DirtyBits.NODE), false, FillRule.NON_ZERO);

    /**
     * Updates a shape node.
     *
     * @param ctx
     * @param shape a shape node
     */
    default void applyMarkerFillableFigureProperties(RenderContext ctx, @NonNull Shape shape) {
        Paint p = Paintable.getPaint(getStyled(MARKER_FILL));
        if (!Objects.equals(shape.getFill(), p)) {
            shape.setFill(p);
        }
        if (shape instanceof Path) {
            ((Path) shape).setFillRule(getStyled(MARKER_FILL_RULE));
        }
    }

}
