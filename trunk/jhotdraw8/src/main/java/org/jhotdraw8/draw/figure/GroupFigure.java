/* @(#)GroupFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jhotdraw8.draw.figure.AbstractCompositeFigure;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.draw.RenderContext;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.handle.MoveHandle;
import org.jhotdraw8.geom.Geom;

/**
 * A figure which groups child figures, so that they can be edited by the user
 * as a unit.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GroupFigure extends AbstractCompositeFigure implements TransformableFigure, HideableFigure, StyleableFigure, LockableFigure {

    /**
     * The CSS type selector for group objects is @code("group"}.
     */
    public final static String TYPE_SELECTOR = "Group";

    @Override
    public void reshape(Transform transform) {
        Point2D oldPoint = new Point2D(0,0);
        Point2D newPoint = getLocalToWorld().createConcatenation(transform).transform(oldPoint);
        
        
        for (Figure child : getChildren()) {
            Transform p2c = child.getParentToLocal();
            if (p2c.isIdentity()) {
                child.reshape(transform);
            } else if (child instanceof TransformableFigure) {
                TransformableFigure tchild=(TransformableFigure) child;
                
                if (transform instanceof Translate) {
                    MoveHandle.translateFigure(child, oldPoint, newPoint, null);
                    /*
                    p2c = tchild.getTransform();
                    Point2D tr = new Point2D(transform.getTx(), transform.getTy());
                    tr = Geom.toDeltaTransform(p2c).transform(tr);
                    Transform t = new Translate(tr.getX(), tr.getY());
                    tchild.reshape(t);*/
                } else {
                /* XXX might need this
               tchild.set(TRANSFORM, Collections.singletonList(child.getLocalToParent()));
                tchild.set(ROTATE, 0.0);
                tchild.set(TRANSLATE_X, 0.0);
                tchild.set(TRANSLATE_Y, 0.0);
                tchild.set(SCALE_X, 1.0);
                tchild.set(SCALE_Y, 1.0);
                */
                    tchild.reshape(p2c.createConcatenation(transform));
                }
            }
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
        ObservableList<Node> group = ((Group) n).getChildren();
        if (!group.equals(nodes)) {
            group.setAll(nodes);
        }
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        Group g = new Group();
        g.setAutoSizeChildren(false);
        return g;
    }

    /**
     * Returns false.
     */
    @Override
    public boolean isLayoutable() {
        return false;
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return null;
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
