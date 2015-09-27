/* @(#)RenderContext.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.scene.Node;
import org.jhotdraw.beans.PropertyBean;
import org.jhotdraw.collection.Key;

/**
 * RenderContext.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface RenderContext extends PropertyBean {
    // ---
    // behavior
    // ---
    /**
     * Gets the JavaFX node which is used to render the specified figure by this
     * {@code RenderContext}.
     *
     * @param f The figure
     * @return The JavaFX node associated to the figure
     */
    public Node getNode(Figure f);

}
