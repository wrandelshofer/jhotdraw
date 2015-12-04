/* @(#)CompositableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import org.jhotdraw.draw.key.BlendModeStyleableFigureKey;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.EffectStyleableFigureKey;

/**
 * Compositable Figure.
 *
 * @author Werner Randelshofer
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
    default void applyCompositableFigureProperties(Node node) {
        node.setBlendMode(getStyled(BLEND_MODE));
        node.setEffect(getStyled(EFFECT));
        node.setOpacity(getStyled(OPACITY));
    }

}
