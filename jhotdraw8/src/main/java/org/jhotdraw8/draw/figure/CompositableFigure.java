/* @(#)CompositableFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.key.BlendModeStyleableFigureKey;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.EffectStyleableFigureKey;

/**
 * Provides properties for compositing a figure.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface CompositableFigure extends Figure {

    /**
     * Specifies a blend mode applied to the figure.
     * <p>
     * Default value: {@code SRC_OVER}.
     */
    public static BlendModeStyleableFigureKey BLEND_MODE = new BlendModeStyleableFigureKey("blendMode", BlendMode.SRC_OVER);
    /**
     * Specifies an effect applied to the figure. The {@code null} value means
     * that no effect is applied.
     * <p>
     * Default value: {@code null}.
     */
    @Nullable
    public static EffectStyleableFigureKey EFFECT = new EffectStyleableFigureKey("effect", null);
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
    public static DoubleStyleableFigureKey OPACITY = new DoubleStyleableFigureKey("opacity", 1.0);

    /**
     * Updates a figure node with all effect properties defined in this
     * interface.
     * <p>
     * Applies the following properties: {@code BLEND_MODE}, {@code EFFECT},
     * {@code OPACITY}.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applyCompositableFigureProperties(@Nonnull Node node) {
        // Performance: JavaFX performs compositing on a Group node, when blend mode != null, altough
        //                    this should be equivalent to SRC_OVER.
        final BlendMode blendMode = getStyled(BLEND_MODE);
        node.setBlendMode(blendMode==BlendMode.SRC_OVER? null:blendMode);
        node.setEffect(getStyled(EFFECT));
        node.setOpacity(getStyled(OPACITY));
    }

}
