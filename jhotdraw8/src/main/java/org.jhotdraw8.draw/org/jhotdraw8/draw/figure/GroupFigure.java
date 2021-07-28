/*
 * @(#)GroupFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.FXTransforms;

import java.util.ArrayList;
import java.util.List;

/**
 * A figure which groups child figures, so that they can be edited by the user
 * as a unit.
 *
 * @author Werner Randelshofer
 */
public class GroupFigure extends AbstractCompositeFigure
        implements Grouping, ResizableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    public static final String TYPE_SELECTOR = "Group";

    @Override
    public @NonNull Node createNode(@NonNull RenderContext drawingView) {
        javafx.scene.Group n = new javafx.scene.Group();
        n.setAutoSizeChildren(false);
        n.setManaged(false);
        return n;
    }

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public void reshapeInLocal(Transform transform) {
        flattenTransforms();
        for (Figure child : getChildren()) {
            child.reshapeInParent(transform);
        }
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        flattenTransforms();
        Transform localTransform = FXTransforms.createReshapeTransform(getCssLayoutBounds(), x, y, width, height);
        for (Figure child : getChildren()) {
            child.reshapeInParent(localTransform);
        }
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node n) {
        applyHideableFigureProperties(ctx, n);
        applyTransformableFigureProperties(ctx, n);
        applyStyleableFigureProperties(ctx, n);
        applyCompositableFigureProperties(ctx, n);

        List<Node> nodes = new ArrayList<>(getChildren().size());
        for (Figure child : getChildren()) {
            nodes.add(ctx.getNode(child));
        }
        ObservableList<Node> group = ((javafx.scene.Group) n).getChildren();
        if (!group.equals(nodes)) {
            group.setAll(nodes);
        }
    }

    @Override
    public boolean isSuitableParent(@NonNull Figure newParent) {
        return true;
    }


    @Override
    public boolean isSuitableChild(@NonNull Figure newChild) {
        return true;
    }
}
