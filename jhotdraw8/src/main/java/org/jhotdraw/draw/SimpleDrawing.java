/* @(#)SimpleDrawing.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import org.jhotdraw.collection.Key;

/**
 * SimpleDrawing.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawing extends GroupFigure implements Drawing {


    public static HashMap<String, Key<?>> getFigureKeys() {
        try {
            HashMap<String, Key<?>> keys = new HashMap<>();
            for (Field f : Drawing.class.getDeclaredFields()) {
                if (Key.class.isAssignableFrom(f.getType())) {
                    Key<?> value = (Key<?>) f.get(null);
                    keys.put(value.getName(), value);
                }
            }
            for (Field f : SimpleDrawing.class.getDeclaredFields()) {
                if (Key.class.isAssignableFrom(f.getType())) {
                    Key<?> value = (Key<?>) f.get(null);
                    keys.put(value.getName(), value);
                }
            }
            return keys;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new InternalError("class can not read its own keys");
        }
    }

    @Override
    public void putNode(DrawingView drawingView) {
        Group g = new Group();
        g.getProperties().put("page", new Rectangle());
        drawingView.putNode(this, g);
    }

    @Override
    public void updateNode(DrawingView v, Node n) {
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

        for (Figure child : children()) {
            children.add(v.getNode(child));
        }
    }

}
