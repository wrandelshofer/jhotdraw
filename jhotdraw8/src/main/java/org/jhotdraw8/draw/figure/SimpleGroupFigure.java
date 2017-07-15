/* @(#)SimpleGroupFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * A figure which groups child figures, so that they can be edited by the user
 * as a unit.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleGroupFigure extends AbstractCompositeFigure
        implements Grouping, ResizableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure {

    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Group";

    @Override
    public Node createNode(RenderContext drawingView) {
        javafx.scene.Group g = new javafx.scene.Group();
        g.setAutoSizeChildren(false);
        return g;
    }

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
    public void updateNode(RenderContext ctx, Node n) {
        applyHideableFigureProperties(n);
        applyTransformableFigureProperties(n);
        applyStyleableFigureProperties(ctx, n);

        List<Node> nodes = new ArrayList<Node>(getChildren().size());
        for (Figure child : getChildren()) {
            nodes.add(ctx.getNode(child));
        }
        ObservableList<Node> group = ((javafx.scene.Group) n).getChildren();
        if (!group.equals(nodes)) {
            group.setAll(nodes);
        }
    }
}
