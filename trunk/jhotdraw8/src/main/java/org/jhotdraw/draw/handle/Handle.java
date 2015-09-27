/* @(#)Handle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import static org.jhotdraw.draw.Figure.*;

/**
 * Handle.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <F> The figure type which this handle supports
 */
public interface Handle<F extends Figure> {

    // ---
    // CSS style classes
    // ---

    /**
     * Style class for handles which draw the outline of a shape.
     */
    public final static String STYLECLASS_HANDLE_OUTLINE = "handle-outline";

    /**
     * Style class for handles which draw the wire frame of a shape for editing.
     */
    public final static String STYLECLASS_HANDLE_WIREFRAME = "handle-wireframe";

    /**
     * Style class for handles which move a shape.
     */
    public final static String STYLECLASS_HANDLE_MOVE = "handle-move";
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

    // ---
    // Behavior
    // ---
    /**
     * Returns the figure to which the handle is associated.
     *
     * @return a figure
     */
    F getFigure();

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

    void updateNode(DrawingView drawingView);

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
     * node.setTranslateZ(f.get(TRANSLATE_Z));
    }
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

}
