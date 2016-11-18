/* @(#)Handle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;

/**
 * Handle.
 *
 * @design.pattern org.jhotdraw8.draw.Drawing Framework, KeyAbstraction.
 * @design.pattern Handle Adapter, Adapter.
 * {@link Handle} adapts the operations for manipulating a {@link Figure} with
 * the mouse to a common interface.
 * @design.pattern org.jhotdraw8.draw.tool.HandleTracker Chain of Responsibility,
 * Handler.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Handle {

    // ---
    // CSS style classes
    // ---
    /**
     * Style class for handles which draw the outline of a shape.
     */
    public final static String STYLECLASS_HANDLE_SELECT_OUTLINE = "handle-select-outline";
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
     * Style class for handles which move multiple shapes.
     */
    public final static String STYLECLASS_HANDLE_MULTI_MOVE = "handle-multi-move";
    /**
     * Style class for handles which draw a point of a shape.
     */
    public final static String STYLECLASS_HANDLE_POINT = "handle-point";
    /**
     * Style class for handles which draw a connection point of a shape.
     */
    public final static String STYLECLASS_HANDLE_CONNECTION_POINT_DISCONNECTED = "handle-connection-point-disconnected";
    /**
     * Style class for handles which draw a connection point of a shape.
     */
    public final static String STYLECLASS_HANDLE_CONNECTION_POINT_CONNECTED = "handle-connection-point-connected";
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
     * @return the node
     */
    Node getNode();

    /** Updates the node. 
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
     * Updates a handle node with all {@code Key}s which define the
     * transformation of the node.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param node a JavaFX scene node. / default void applyFigureTransform(Node
     * node) { Figure f = getFigure(); node.setRotate(f.get(ROTATE));
     * node.setRotationAxis(f.get(ROTATION_AXIS));
     * node.setScaleX(f.get(SCALE_X)); node.setScaleY(f.get(SCALE_Y));
     * node.setScaleZ(f.get(SCALE_Z)); node.setTranslateX(f.get(TRANSLATE_X));
     * node.setTranslateY(f.get(TRANSLATE_Y));
     * node.setTranslateZ(f.get(TRANSLATE_Z)); }
     */
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
    
    /** Returns true if that handle is compatible with this handle.
     * @param that the other handle
     * @return true if compatible */
    boolean isCompatible(Handle that);

    /** The cursor that should be shown when the mouse hovers over a selectable
     * handle.
     * Non-selectable handles should return null.
     * 
     * @return the cursor
     */
    Cursor getCursor();
    
    /** The pick location of the handle in view coordinates.
     * 
     * @return the pick location or null if the handle is not interactive
     */
    Point2D getLocationInView();
}
