/* @(#)Layer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.draw.figure.Figure;

/**
 * Defines a <i>layer</i> of a {@link Drawing}.
 * <p>
 * The parent of a {@code Layer} must be a {@code Drawing} or a {@code Clipping} . Method
 * {@link #isSuitableParent(org.jhotdraw8.draw.figure.Figure)} must be
 * implementend accordingly.
 * <p>
 * A layer does not have handles and is not selectable.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Layer extends Figure {

    /**
     * The CSS type selector for a layer figure is {@code "layer"}.
     */
    public final static String TYPE_SELECTOR = "Layer";

    /**
     * Layer figures always return false for isSelectable.
     */
    @Override
    default public boolean isSelectable() {
        return false;
    }

    @Override
    default String getTypeSelector() {
        return TYPE_SELECTOR;
    }
    
        @Override
    default boolean isAllowsChildren() {
        return true;
    }
    
   @Override
    default boolean isSuitableParent(Figure newParent) {
        return newParent == null || (newParent instanceof Drawing) || (newParent instanceof Clipping);
    }
}
