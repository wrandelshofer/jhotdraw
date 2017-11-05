/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.draw.figure;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.render.RenderingIntent;

/**
 * SimpleLayer.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleLayer extends AbstractCompositeFigure
        implements Layer, StyleableFigure, HideableFigure, LockableFigure, NonTransformableFigure {

    @Override
    public void reshapeInLocal(Transform transform) {
        for (Figure child : getChildren()) {
            child.reshapeInLocal(transform);
        }
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Group n = (Group) node;
        applyHideableFigureProperties(n);
        if (!isVisible()) {
            return;
        }

        if (getChildren().size() > 5000 && ctx.get(RenderContext.RENDERING_INTENT) == RenderingIntent.EDITOR) {
            if (n.getChildren().size() != 1 || (n.getChildren().get(0) instanceof Text)) {
                Text text = new Text();
                text.setText("Layer " + getId() + " has too many children.");
                text.setFill(Color.RED);
                text.setX(20);
                if (getParent() != null) {
                    text.setY(20 + 20 * getParent().getChildren().indexOf(this));
                } else {
                    text.setY(20);
                }
                n.getChildren().setAll(text);
            }
        } else {
            applyStyleableFigureProperties(ctx, n);
            List<Node> nodes = new ArrayList<>(getChildren().size());
            for (Figure child : getChildren()) {
                nodes.add(ctx.getNode(child));
            }
            ObservableList<Node> group = n.getChildren();
            if (!group.equals(nodes)) {
                group.setAll(nodes);
            }
        }
    }

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
    protected void checkNewParent(Figure newValue) {
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
    public void createHandles(HandleType handleType, List<Handle> list) {
        // empty
    }

}
