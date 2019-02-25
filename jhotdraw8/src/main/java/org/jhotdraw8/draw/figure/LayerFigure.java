/* @(#)LayerFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;

import java.util.ArrayList;
import java.util.List;

/**
 * LayerFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LayerFigure extends AbstractCompositeFigure
        implements Layer, StyleableFigure, HideableFigure, LockableFigure, NonTransformableFigure {

    @Override
    public void reshapeInLocal(Transform transform) {
        for (Figure child : getChildren()) {
            child.reshapeInLocal(transform);
        }
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        // empty
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        Group n = (Group) node;
        applyHideableFigureProperties(ctx, n);
        if (!isVisible()) {
            return;
        }
        applyStyleableFigureProperties(ctx, n);

        List<Node> childNodes = new ArrayList<>(getChildren().size());

        int maxNodesPerLayer = ctx.get(RenderContext.MAX_NODES_PER_LAYER);
        Bounds clipBounds = ctx.get(RenderContext.CLIP_BOUNDS);
        if (ctx.get(RenderContext.RENDERING_INTENT) == RenderingIntent.EDITOR
                && clipBounds != null /* && getChildren().size() > maxNodesPerLayer*/) {

            for (Figure child : getChildren()) {
                if (child.getBoundsInWorld().intersects(clipBounds)) {
                    Node childNode = ctx.getNode(child);
                    childNodes.add(childNode);
                }
            }

            if (childNodes.size() > maxNodesPerLayer) {
                Text text = new Text();
                text.setText("Layer \"" + getId() + "\" has too many children: " + getChildren().size());
                text.setFill(Color.RED);
                text.setX(clipBounds.getMinX() + 20);
                if (getParent() != null) {
                    text.setY(clipBounds.getMinY() + 20 * getParent().getChildren().indexOf(this));
                } else {
                    text.setY(clipBounds.getMinY() + 20);
                }
                childNodes.clear();
                childNodes.add(text);
            }
        } else {
            for (Figure child : getChildren()) {
                childNodes.add(ctx.getNode(child));
            }
        }

        ObservableList<Node> groupChildren = n.getChildren();
        if (!groupChildren.equals(childNodes)) {
            groupChildren.setAll(childNodes);
        }
    }

    @Nonnull
    @Override
    public Node createNode(RenderContext ctx) {
        Group n = new Group();
        return n;
    }

    /**
     * This method throws an illegal argument exception if the new parent is not
     * an instance of Drawing.
     *
     * @param newValue the desired parent
     */
    protected void checkNewParent(@Nullable Figure newValue) {
        if (newValue != null && !(newValue instanceof Drawing) && !(newValue instanceof Clipping)) {
            throw new IllegalArgumentException("A Layer can only be added as a child to a Drawing. Illegal parent: "
                    + newValue);
        }
    }

    /**
     * Layer figures always return false for isSelectable.
     *
     * @return false
     */
    @Override
    public boolean isSelectable() {
        return false;
    }

    /**
     * This method whether the provided figure is a suitable parent for this
     * figure.
     * <p>
     * This implementation returns true if {@code newParent} is a
     * {@link Drawing}.
     *
     * @param newParent The new parent figure.
     * @return true if {@code newParent} is an acceptable parent
     */
    @Override
    public boolean isSuitableParent(Figure newParent) {
        return (newParent instanceof Drawing);
    }

    /**
     * Layers never create handles.
     */
    @Override
    public void createHandles(HandleType handleType, @Nonnull List<Handle> list) {
        // empty
    }

}
