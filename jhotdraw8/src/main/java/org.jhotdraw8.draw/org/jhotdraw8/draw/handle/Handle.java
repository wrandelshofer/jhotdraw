/* @(#)Handle.java
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
 * @version $Id$
 * @design.pattern org.jhotdraw8.draw.figure.Drawing Framework, KeyAbstraction.
 * @design.pattern Handle Adapter, Adapter. {@link Handle} adapts the operations
 * for manipulating a {@link Figure} with the mouse to a common interface.
 * @design.pattern org.jhotdraw8.draw.tool.HandleTracker Chain of
 * Responsibility, Handler.
 */
public interface Handle {

    // ---
    // CSS style classes
    // ---
    /**
     * Style class for all handles.
     */
    public final static String STYLECLASS_HANDLE = "handle";
    /**
     * Style class for handles which draw the outline of a shape.
     */
    public final static String STYLECLASS_HANDLE_SELECT_OUTLINE = "handle-select-outline";
    /**
     * Style class for handles which draw the outline of a shape.
     */
    public final static String STYLECLASS_HANDLE_LEAD_OUTLINE = "handle-lead-outline";
    /**
     * Style class for handles which draw the outline of a shape.
     */
    public final static String STYLECLASS_HANDLE_ANCHOR_OUTLINE = "handle-anchor-outline";
    /**
     * Style class for handles which draw the outline of multiple selection.
     */
    public final static String STYLECLASS_HANDLE_MULTI_SELECT_OUTLINE = "handle-multi-select-outline";

    /**
     * Style class for handles which draw the wire frame of a shape for editing.
     */
    public final static String STYLECLASS_HANDLE_MOVE_OUTLINE = "handle-move-outline";

    /**
     * Style class for handles which draw the wire frame of a shape for editing.
     */
    public final static String STYLECLASS_HANDLE_RESIZE_OUTLINE = "handle-resize-outline";
    /**
     * Style class for handles which draw the wire frame of a shape for editing.
     */
    public final static String STYLECLASS_HANDLE_TRANSFORM_OUTLINE = "handle-transform-outline";

    /**
     * Style class for handles which move a shape.
     */
    public final static String STYLECLASS_HANDLE_MOVE = "handle-move";
    /**
     * Style class for handles which do not move a shape.
     */
    public final static String STYLECLASS_HANDLE_MOVE_LOCKED = "handle-move-locked";
    /**
     * Style class for handles which move multiple shapes.
     */
    public final static String STYLECLASS_HANDLE_MULTI_MOVE = "handle-multi-move";
    /**
     * Style class for handles which draw a point of a shape.
     */
    public final static String STYLECLASS_HANDLE_POINT = "handle-point";
    /**
     * Style class for handles which draw a point of a shape.
     */
    public final static String STYLECLASS_HANDLE_POINT_CONNECTED = "handle-point-connected";
    /**
     * Style class for handles which draw a point of a shape.
     */
    public final static String STYLECLASS_HANDLE_POINT_OUTLINE = "handle-point-outline";
    /**
     * Style class for handles which draw a point of a shape.
     */
    public final static String STYLECLASS_HANDLE_CONTROL_POINT_OUTLINE = "handle-control-point-outline";
    /**
     * Style class for handles which draw a point of a shape.
     */
    public final static String STYLECLASS_HANDLE_CONTROL_POINT = "handle-control-point";
    /**
     * Style class for handles which draw a connection point of a shape.
     */
    public final static String STYLECLASS_HANDLE_CONNECTION_POINT_DISCONNECTED = "handle-connection-point-disconnected";
    /**
     * Style class for handles which draw a connection point of a shape.
     */
    public final static String STYLECLASS_HANDLE_CONNECTION_POINT_CONNECTED = "handle-connection-point-connected";
    /**
     * Style class for handles which draw a connection line of a shape.
     */
    public final static String STYLECLASS_HANDLE_CONNECTION_LINE_CONNECTED = "handle-connection-line-connected";
    /**
     * Style class for handles which allow to resize a shape.
     */
    public final static String STYLECLASS_HANDLE_RESIZE = "handle-resize";
    /**
     * Style class for handles which allow to scale and translate a shape.
     */
    public final static String STYLECLASS_HANDLE_SCALE_TRANSLATE = "handle-scale-translate";
    /**
     * Style class for handles which allow to rotate a shape.
     */
    public final static String STYLECLASS_HANDLE_ROTATE = "handle-rotate";
    /**
     * Style class for handles which mark the pivot point of a transformation.
     */
    public final static String STYLECLASS_HANDLE_PIVOT = "handle-pivot";
    /**
     * Style class for handles for custom features.
     */
    public final static String STYLECLASS_HANDLE_CUSTOM = "handle-custom";

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
    default void handleMouseDragged(MouseEvent event, DrawingView dv) {
    }

    default void handleMouseReleased(MouseEvent event, DrawingView dv) {
    }

    default void handleMousePressed(MouseEvent event, DrawingView dv) {
    }

    default void handleKeyPressed(KeyEvent event, DrawingView dv) {
    }

    default void handleKeyReleased(KeyEvent event, DrawingView dv) {
    }

    default void handleKeyTyped(KeyEvent event, DrawingView dv) {
    }

    default void handleMouseClicked(MouseEvent event, DrawingView dv) {

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
     * @param dv               the drawing view
     * @param x                the point
     * @param y                the point
     * @param tolerance        the tolerance (radius around the point)
     * @param toleranceSquared the squared tolerance (squared radius around the point)
     * @return true if we picked the handle
     */
    default boolean contains(DrawingView dv, double x, double y, double tolerance, double toleranceSquared) {
        return contains(dv, x, y, toleranceSquared);
    }

    /**
     * Whether the user picked the handle.
     *
     * @param dv               the drawing view
     * @param x                the point
     * @param y                the point
     * @param toleranceSquared the squared tolerance (squared radius around the point)
     * @return true if we picked the handle
     */
    boolean contains(DrawingView dv, double x, double y, double toleranceSquared);


    /**
     * Returns true if this handle is editable.
     *
     * @return the default implementation returns true if the owner is editable
     */
    default boolean isEditable() {
        return getOwner().isEditable();
    }
}
