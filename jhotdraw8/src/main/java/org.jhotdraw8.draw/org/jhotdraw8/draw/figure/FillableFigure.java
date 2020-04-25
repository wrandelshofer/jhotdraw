/*
 * @(#)FillableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;
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
public interface FillableFigure extends Figure {

    /**
     * Defines the paint used for filling the interior of the figure.
     * <p>
     * Default value: {@code Color.WHITE}.
     */
    NullablePaintableStyleableKey FILL = new NullablePaintableStyleableKey("fill", new CssColor("canvas", Color.WHITE));

    /**
     * Updates a shape node.
     *
     * @param ctx   the render context
     * @param shape a shape node
     */
    default void applyFillableFigureProperties(@Nullable RenderContext ctx, @NonNull Shape shape) {
        Paint p = Paintable.getPaint(getStyled(FILL), ctx);
        if (!Objects.equals(shape.getFill(), p)) {
            shape.setFill(p);
        }
    }

}
