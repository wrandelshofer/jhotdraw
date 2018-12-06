/* @(#)SimpleGroupFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;

import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Transforms;

/**
 * A figure which groups child figures, so that they can be edited by the user
 * as a unit.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleGroupFigure extends AbstractCompositeFigure
        implements Grouping, ResizableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Group";

    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        javafx.scene.Group g = new javafx.scene.Group();
        g.setAutoSizeChildren(false);
        return g;
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public void reshapeInLocal(Transform transform) {
        // XXX if one of the children is non-transformable, we should not reshapeInLocal at all!
        flattenTransforms();
        Transform localTransform = transform;
        //Transform localTransform = transform.createConcatenation(getParentToLocal());
        for (Figure child : getChildren()) {
            child.reshapeInParent(localTransform);
        }
    }
    
    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        // XXX if one of the children is non-transformable, we should not reshapeInLocal at all!
        flattenTransforms();
        Transform localTransform = Transforms.createReshapeTransform(getCssBoundsInLocal(), x, y, width, height);
        //Transform localTransform = transform.createConcatenation(getParentToLocal());
        for (Figure child : getChildren()) {
            child.reshapeInParent(localTransform);
        }
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node n) {
        applyHideableFigureProperties(n);
        applyTransformableFigureProperties(ctx, n);
        applyStyleableFigureProperties(ctx, n);
        applyCompositableFigureProperties(n);
        
        List<Node> nodes = new ArrayList<>(getChildren().size());
        for (Figure child : getChildren()) {
            nodes.add(ctx.getNode(child));
        }
        ObservableList<Node> group = ((javafx.scene.Group) n).getChildren();
        if (!group.equals(nodes)) {
            group.setAll(nodes);
        }
    }
}
