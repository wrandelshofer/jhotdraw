/*
 * @(#)Tracker.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.DrawingView;

/**
 * Tracker.
 *
 * @author Werner Randelshofer
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
     * Returns the node which presents the tracker.
     *
     * @return a node
     */
    @NonNull Node getNode();

    /**
     * Handle input event forwarded from the parent Tool.
     *
     * @param evt  an event
     * @param view a view
     */
    void trackMousePressed(MouseEvent evt, DrawingView view);

    /**
     * Handle input event forwarded from the parent Tool.
     *
     * @param evt  an event
     * @param view a view
     */
    void trackMouseReleased(MouseEvent evt, DrawingView view);

    /**
     * Handle input event forwarded from the parent Tool.
     *
     * @param evt  an event
     * @param view a view
     */
    void trackMouseClicked(MouseEvent evt, DrawingView view);

    /**
     * Handle input event forwarded from the parent Tool.
     *
     * @param evt  an event
     * @param view a view
     */
    void trackMouseDragged(MouseEvent evt, DrawingView view);

    void trackKeyPressed(KeyEvent event, DrawingView view);

    void trackKeyReleased(KeyEvent event, DrawingView view);

    void trackKeyTyped(KeyEvent event, DrawingView view);
}
