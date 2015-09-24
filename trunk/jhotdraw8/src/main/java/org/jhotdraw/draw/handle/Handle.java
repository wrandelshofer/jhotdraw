/* @(#)Handle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.handle;

import javafx.scene.Node;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import static org.jhotdraw.draw.Figure.*;
/**
 * Handle.
 *
 * @author Werner Randelshofer
 * @version $Id$
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
    Figure getFigure();

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
     * Updates a handle node with all {@code Key}s which define the transformation
     * of the node.
     * <p>
     * This method is intended to be used by {@link #updateNode}.
     *
     * @param node a JavaFX scene node.
     */
    default void applyFigureTransform(Node node) {
        Figure f = getFigure();
        node.setRotate(f.get(ROTATE));
        node.setRotationAxis(f.get(ROTATION_AXIS));
        node.setScaleX(f.get(SCALE_X));
        node.setScaleY(f.get(SCALE_Y));
        node.setScaleZ(f.get(SCALE_Z));
        node.setTranslateX(f.get(TRANSLATE_X));
        node.setTranslateY(f.get(TRANSLATE_Y));
        node.setTranslateZ(f.get(TRANSLATE_Z));
    }
    
    /**
     * Disposes of all resources acquired by the handler.
     */
    void dispose();
}
