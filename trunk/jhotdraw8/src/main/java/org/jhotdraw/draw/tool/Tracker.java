/*
 * @(#)Tracker.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.jhotdraw.beans.PropertyBean;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.event.Listener;

/**
 * Tracker.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Tracker {

    // ---
    // Property Names
    // ---
    // ---
    // Properties
    // ---
    // ---
    // Behaviors
    // ---

    /**
     * Returns the node which presents tracker.
     *
     * @return a node
     */
    Node getNode();

    /**
     * Handle input event forwarded from the parent Tool.
     *
     * @param evt an event
     * @param view a view
     */
    void trackMousePressed(MouseEvent evt, DrawingView view);

    /**
     * Handle input event forwarded from the parent Tool.
     *
     * @param evt an event
     * @param view a view
     */
    void trackMouseReleased(MouseEvent evt, DrawingView view);

    /**
     * Handle input event forwarded from the parent Tool.
     *
     * @param evt an event
     * @param view a view
     */
    void trackMouseDragged(MouseEvent evt, DrawingView view);
}
