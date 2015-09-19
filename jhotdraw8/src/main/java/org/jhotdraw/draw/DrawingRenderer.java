/* @(#)DrawingRenderer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.scene.Node;

/**
 * DrawingRenderer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DrawingRenderer {

    /**
     * Gets the node which is used to render the specified figure by this
     * {@code DrawingRenderer}.
     *
     * @param f The figure
     * @return The node associated to the figure
     */
    public Node getNode(Figure f);

}
