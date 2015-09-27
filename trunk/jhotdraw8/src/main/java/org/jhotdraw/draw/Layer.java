/* @(#)Layer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw;

/**
 * Defines a <i>layer</i> of a {@link Drawing}.
 * <p>
 * The parent of a {@code Layer} must be a {@code Drawing}.
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
    public final static String TYPE_SELECTOR = "layer";
    
    /** Layer figures always return false for isSelectable. */
    @Override
    default public boolean isSelectable() { return false; }
    
    @Override
    default String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}

