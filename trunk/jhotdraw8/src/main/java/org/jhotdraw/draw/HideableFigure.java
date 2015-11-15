/* @(#)HideableFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw;

import javafx.scene.Node;
import org.jhotdraw.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;

/**
 * HideableFigure.
 * @author Werner Randelshofer
 */
public interface HideableFigure extends Figure {
    /**
     * Defines the visibility of the figure. Default value: {@code true}.
     */
    public static BooleanStyleableFigureKey VISIBLE = new BooleanStyleableFigureKey("visible", DirtyMask.of(DirtyBits.NODE), true);
    /**
     * Updates a figure node with all style and effect properties defined in
     * this interface.
     * <p>
     * Applies the following properties: {@code STYLE_ID}, {@code VISIBLE}.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param node a node which was created with method {@link #createNode}.
     */
    default void applyHideableFigureProperties(Node node) {
        node.setVisible(getStyled(VISIBLE));
    }

}
