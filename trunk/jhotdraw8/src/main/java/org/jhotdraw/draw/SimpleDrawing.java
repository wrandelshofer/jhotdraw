/* @(#)SimpleDrawing.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;

/**
 * SimpleDrawing.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawing extends AbstractCompositeFigure implements Drawing {

    @Override
    public Node createNode(RenderContext drawingView) {
        Group g = new Group();
        g.getProperties().put("page", new Rectangle());
        return g;
    }

    @Override
    public void updateNode(RenderContext v, Node n) {
        Group g = (Group) n;
        ObservableList<Node> children = ((Group) n).getChildren();
        children.clear();
        Rectangle2D bounds = get(BOUNDS);
        Rectangle page = (Rectangle) g.getProperties().get("page");
        page.setX(bounds.getMinX());
        page.setY(bounds.getMinY());
        page.setWidth(bounds.getWidth());
        page.setHeight(bounds.getHeight());
        page.setFill(get(BACKGROUND));
        children.add(page);

        for (Figure child : childrenProperty()) {
            children.add(v.getNode(child));
        }
    }

    @Override
    public Bounds getBoundsInLocal() {
        Rectangle2D bounds = get(BOUNDS);
        return new BoundingBox(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
        
    }    @Override
    public void reshape(Transform transform) {
        for (Figure child : childrenProperty()) {
            child.reshape(transform);
        }
    }

    @Override
    public boolean isLayoutable() {
        return false;
    }
}
