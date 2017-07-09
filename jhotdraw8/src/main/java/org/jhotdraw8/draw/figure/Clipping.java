/* @(#)Layer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.net.URI;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Figure;

/**
 * Defines a <i>clipping</i> of a {@link Drawing}.
 * <p>
 * A clipping is used to hold a selection of figures, so that they can be read
 * or written to the clipboard.
 * <p>
 * A clipping can not have a parent, and thus returns false in
 * isSuitableParent(parent) for all parents except null.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Clipping extends Figure {

    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Clipping";

    /**
     * Clipping figures always return false for isSelectable.
     */
    @Override
    default public boolean isSelectable() {
        return false;
    }

    @Override
    default String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
