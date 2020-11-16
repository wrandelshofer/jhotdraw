/*
 * @(#)SvgInheritableFigureAttributes.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.figure;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.CssPercentageConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.key.DoubleStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * The following attributes can be defined on all SVG figures using the "defaulting"
 * mechanism.
 */
public interface SvgCompositableFigure extends Figure {
    /**
     * opacity.
     * <a href="https://www.w3.org/TR/2011/REC-SVG11-20110816/masking.html#ObjectAndGroupOpacityProperties">link</a>
     */
    @NonNull DoubleStyleableKey OPACITY_KEY = new DoubleStyleableKey("opacity", 1.0, new CssPercentageConverter(false));

    /**
     * Updates a figure node with all effect properties defined in this
     * interface.
     * <p>
     * Applies the following properties:
     * {@link #OPACITY_KEY}.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param ctx  the render context
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applySvgCompositableFigureProperties(RenderContext ctx, @NonNull Node node) {
        node.setOpacity(getStyledNonNull(OPACITY_KEY));
    }
}
