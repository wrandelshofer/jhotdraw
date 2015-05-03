/* @(#)Handle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import java.util.Optional;
import javafx.scene.Node;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.event.Listener;

/**
 * Handle.
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Handle {
    // ---
    // Behavior
    // ---
    /** Returns the figure to which the handle is associated. */
    public Figure getFigure();
    
    /** Returns the node which is used to visualize the handle.
     * The node is rendered by {@code DrawingView} in a pane which uses view coordinates.
     * The node should use {@code DrawingView.viewToDrawingProperty()} to transform
     * its coordinates.
     *
     * @return  the node */
    public Node getNode();

    /**
     * Disposes of all resources acquired by the handler.
     */
    public void dispose();

    
    /**
     * This method is invoked by {@code Tool} (typically the {@code HandleTool}),
     * when the user drags the handle. 
    *
    * @param dx Drag distance in x direction (in drawing coordinates).
    * @param dy Drag distance in y direction (in drawing coordinates).
    */
    public void onMouseDragged(double dx, double dy);

        /**
     * Returns true, if this handle is combinable with the specified handle.
     * This method is used to determine, if multiple handles need to be tracked,
     * when more than one figure is selected.
     */
    public boolean isCombinableWith(Handle handle);
    // ---
    // listeners
    // ---
    void addHandleListener(Listener<org.jhotdraw.draw.handle.HandleEvent> listener);

    void removeHandleListener(Listener<org.jhotdraw.draw.handle.HandleEvent> listener);

}
