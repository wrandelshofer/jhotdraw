/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.draw;

import java.util.ArrayList;
import java.util.Collection;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.figure.AbstractCompositeFigure;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.figure.LockedFigure;
import org.jhotdraw8.draw.figure.NonTransformableFigure;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;

/**
 * SimpleLayer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleClipping extends AbstractCompositeFigure
        implements Clipping, StyleableFigure, LockedFigure, NonTransformableFigure {

    public SimpleClipping() {
    }
    public SimpleClipping(Collection<Figure> children) {
        getChildren().addAll(children);
    }

    @Override
    public void reshapeInLocal(Transform transform) {
        for (Figure child : getChildren()) {
            child.reshapeInLocal(transform);
        }
    }

    @Override
    public void updateNode(RenderContext ctx, Node n) {
        applyStyleableFigureProperties(ctx, n);

        List<Node> nodes = new ArrayList<Node>(getChildren().size());
        for (Figure child : getChildren()) {
            nodes.add(ctx.getNode(child));
        }
        ObservableList<Node> group = ((Group) n).getChildren();
        if (!group.equals(nodes)) {
            group.setAll(nodes);
        }
    }

    @Override
    public Node createNode(RenderContext ctx) {
        Group n = new Group();
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

    /** Returns false. */
    @Override
    public boolean isLayoutable() {
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
        return newParent== null;
    }

    /**
     * Layers never create handles.
     */
    @Override
    public void createHandles(HandleType handleType, DrawingView dv, List<Handle> list) {
        // empty
    }

}
