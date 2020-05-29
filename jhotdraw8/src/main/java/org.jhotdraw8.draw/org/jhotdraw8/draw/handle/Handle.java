/*
 * @(#)Handle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;

/**
 * Handle.
 *
 * @author Werner Randelshofer
 * @design.pattern org.jhotdraw8.draw.figure.Drawing Framework, KeyAbstraction.
 * @design.pattern Handle Adapter, Adapter. {@link Handle} adapts the operations
 * for manipulating a {@link Figure} with the mouse to a common interface.
 * @design.pattern org.jhotdraw8.draw.tool.HandleTracker Chain of
 * Responsibility, Handler.
 */
public interface Handle {
    // ---
    // Behavior
    // ---

    /**
     * Returns the figure to which the handle is associated.
     *
     * @return a figure
     */
    Figure getOwner();

    /**
     * Returns the node which is used to visualize the handle. The node is
     * rendered by {@code DrawingView} in a pane which uses view coordinates.
     * The node should use {@code DrawingView.viewToDrawingProperty()} to
     * transform its coordinates.
     * <p>
     * A {@code Handle} can only reside in one {@code DrawingView} at any given
     * time. The JavaFX node returned by this method is use to render the handle
     * in the {@code DrawingView}. This is why, unlike {@code Figure}, we only
     * need this method instead of a {@code createNode} and an
     * {@code updateNode} method.
     *
     * @param view the drawing view
     * @return the node
     */
    Node getNode(DrawingView view);

    /**
     * Updates the node.
     *
     * @param drawingView the drawing view
     */
    void updateNode(DrawingView drawingView);

    /**
     * Whether the handle is selectable.
     *
     * @return true if selectable
     */
    boolean isSelectable();

    /**
     * Disposes of all resources acquired by the handler.
     */
    void dispose();

    // ---
    // Event handlers
    // ----
    default void onMouseDragged(MouseEvent event, DrawingView dv) {
    }

    default void onMouseReleased(MouseEvent event, DrawingView dv) {
    }

    default void onMousePressed(MouseEvent event, DrawingView dv) {
    }

    default void onKeyPressed(KeyEvent event, DrawingView dv) {
    }

    default void onKeyReleased(KeyEvent event, DrawingView dv) {
    }

    default void onKeyTyped(KeyEvent event, DrawingView dv) {
    }

    default void onMouseClicked(MouseEvent event, DrawingView dv) {

    }

    /**
     * Returns true if that handle is compatible with this handle.
     *
     * @param that the other handle
     * @return true if compatible
     */
    boolean isCompatible(Handle that);

    /**
     * The cursor that should be shown when the mouse hovers over a selectable
     * handle. Non-selectable handles should return null.
     *
     * @return the cursor
     */
    @Nullable Cursor getCursor();

    /**
     * Whether the user picked the handle.
     *
     * @param dv        the drawing view
     * @param x         the point
     * @param y         the point
     * @param tolerance the tolerance (radius around the point)
     * @return true if we picked the handle
     */
    boolean contains(DrawingView dv, double x, double y, double tolerance);


    /**
     * Returns true if this handle is editable.
     *
     * @return the default implementation returns true if the owner is editable
     */
    default boolean isEditable() {
        return getOwner().isEditable();
    }
}
