/*
 * @(#)CompositableFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.CssPercentageConverter;
import org.jhotdraw8.draw.key.BlendModeStyleableKey;
import org.jhotdraw8.draw.key.DoubleStyleableKey;
import org.jhotdraw8.draw.key.EffectStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * Provides properties for compositing a figure.
 * <p>
 * Usage:
 * <pre>
 * class MyFigureClass implements CompositableFigure {
 *     public void updateNode(RenderContext ctx, Node n) {
 *         applyCompositableFigureProperties(ctx, n);
 *     }
 * }
 * </pre>
 *
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Mixin, Traits.
 */
public interface CompositableFigure extends Figure {

    /**
     * Specifies a blend mode applied to the figure.
     * <p>
     * Default value: {@code SRC_OVER}.
     */
    @NonNull BlendModeStyleableKey BLEND_MODE = new BlendModeStyleableKey("blendMode", BlendMode.SRC_OVER);
    /**
     * Specifies an effect applied to the figure. The {@code null} value means
     * that no effect is applied.
     * <p>
     * Default value: {@code null}.
     */
    @NonNull EffectStyleableKey EFFECT = new EffectStyleableKey("effect", null);
    /**
     * Specifies the opacity of the figure. A figure with {@code 0} opacity is
     * completely translucent. A figure with {@code 1} opacity is completely
     * opaque.
     * <p>
     * Values smaller than {@code 0} are treated as {@code 0}. Values larger
     * than {@code 1} are treated as {@code 1}.
     * <p>
     * Default value: {@code 1}.
     */
    @NonNull DoubleStyleableKey OPACITY = new DoubleStyleableKey("opacity", 1.0, new CssPercentageConverter(false));

    /**
     * Updates a figure node with all effect properties defined in this
     * interface.
     * <p>
     * Applies the following properties: {@link #BLEND_MODE}, {@link #EFFECT},
     * {@link #OPACITY}.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param ctx  the render context
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applyCompositableFigureProperties(RenderContext ctx, @NonNull Node node) {
        // Performance: JavaFX performs compositing on a Group node,
        // when blend mode != null, although this should be equivalent to SRC_OVER.
        final BlendMode blendMode = getStyled(BLEND_MODE);
        node.setBlendMode(blendMode == BlendMode.SRC_OVER ? null : blendMode);
        node.setEffect(getStyled(EFFECT));
        node.setOpacity(getStyledNonNull(OPACITY));
    }

}
