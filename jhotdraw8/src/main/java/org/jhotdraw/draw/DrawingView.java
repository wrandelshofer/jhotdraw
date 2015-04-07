/* @(#)DrawingView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

/**
 * A {@code DrawingView} can display a {@code Drawing}.
 * <p>
 * A {@code DrawingView} consists of the following layers:
 * <ul>
 * <li>Pages. Displays the pages of the drawing.</li>
 * <li>Drawing. Displays the figures of the drawing.</li>
 * <li>Handles. Displays the handles used for editing figures.</li>
 * <li>Grid. Displays a grid.</li>
 * </ul>
 * 
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DrawingView {

    ObjectProperty<Drawing> drawing();

    default void setDrawing(Drawing newValue) {
        drawing().set(newValue);
    }

    default Drawing getDrawing() {
        return drawing().get();
    }

    /** Puts a node for the specified figure into the drawing view.
     * The drawing view uses this node to build a scene graph.
     * The node can not be part of multiple drawing views.
     */
    public void putNode(Figure f, Node newNode);

    /** Gets the node which is used to render the specified figure by the drawing view.
     */
    public Node getNode(Figure f);

}
