/* @(#)Tracker.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.annotation.Nonnull;
import org.jhotdraw8.draw.DrawingView;

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
     * Returns the node which presents the tracker.
     *
     * @return a node
     */@Nonnull 
    Node getNode();

    /**
     * Handle input event forwarded from the parent Tool.
     *
     * @param evt an event
     * @param view a view
     */
    void trackMousePressed(@Nonnull MouseEvent evt, @Nonnull DrawingView view );

    /**
     * Handle input event forwarded from the parent Tool.
     *
     * @param evt an event
     * @param view a view
     */
    void trackMouseReleased(@Nonnull MouseEvent evt, @Nonnull DrawingView view );

    /**
     * Handle input event forwarded from the parent Tool.
     *
     * @param evt an event
     * @param view a view
     */
    void trackMouseClicked(@Nonnull MouseEvent evt, @Nonnull DrawingView view );

    /**
     * Handle input event forwarded from the parent Tool.
     *
     * @param evt an event
     * @param view a view
     */
    void trackMouseDragged(@Nonnull MouseEvent evt, @Nonnull DrawingView view );

    void trackKeyPressed(@Nonnull KeyEvent event, @Nonnull DrawingView view );

    void trackKeyReleased(@Nonnull KeyEvent event, @Nonnull DrawingView view );

    void trackKeyTyped(@Nonnull KeyEvent event, @Nonnull DrawingView view );
}
