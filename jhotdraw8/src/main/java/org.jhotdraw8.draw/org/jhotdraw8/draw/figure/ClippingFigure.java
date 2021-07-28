/*
 * @(#)ClippingFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.render.RenderContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ClippingFigure.
 *
 * @author Werner Randelshofer
 */
public class ClippingFigure extends AbstractCompositeFigure
        implements Clipping, StyleableFigure, LockedFigure, NonTransformableFigure {

    public ClippingFigure() {
    }

    public ClippingFigure(@NonNull Collection<Figure> children) {
        getChildren().addAll(children);
    }

    @Override
    public void reshapeInLocal(@NonNull Transform transform) {
        for (Figure child : getChildren()) {
            child.reshapeInLocal(transform);
        }
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        // empty
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node n) {
        applyStyleableFigureProperties(ctx, n);

        List<Node> nodes = new ArrayList<>(getChildren().size());
        for (Figure child : getChildren()) {
            nodes.add(ctx.getNode(child));
        }
        ObservableList<Node> group = ((Group) n).getChildren();
        if (!group.equals(nodes)) {
            group.setAll(nodes);
        }
    }

    @Override
    public @NonNull Node createNode(@NonNull RenderContext ctx) {
        Group n = new Group();
        n.setManaged(false);
        n.setAutoSizeChildren(false);
        return n;
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
     * This method returns false for all new parents.
     *
     * @param newParent The new parent figure.
     * @return false
     */
    @Override
    public boolean isSuitableParent(@NonNull Figure newParent) {
        return false;
    }

    /**
     * This method returns true for all children.
     *
     * @param newChild The new child figure.
     * @return true
     */
    @Override
    public boolean isSuitableChild(@NonNull Figure newChild) {
        return true;
    }

    /**
     * Layers never create handles.
     */
    @Override
    public void createHandles(@NonNull HandleType handleType, @NonNull List<Handle> list) {
        // empty
    }

    @Override
    public @NonNull Bounds getBoundsInLocal() {
        return getLayoutBounds();
    }
}
