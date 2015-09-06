/* @(#)GroupFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import static java.lang.Math.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;

/**
 * GroupFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GroupFigure extends AbstractCompositeFigure {


    @Override
    public Bounds getLayoutBounds() {
        // FIXME we should cache the layout bounds
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Figure child : childrenProperty()) {
            Bounds b = child.getLayoutBounds();
            minX = min(minX, b.getMinX());
            maxX = max(maxX, b.getMaxX());
            minY = min(minY, b.getMinY());
            maxY = max(maxY, b.getMaxY());
        }

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public void reshape(Transform transform) {
        for (Figure child : childrenProperty()) {
            child.reshape(transform);
        }
    }

    @Override
    public void updateNode(DrawingView v, Node n) {
        applyFigureProperties(n);
        ObservableList<Node> group = ((Group) n).getChildren();
        group.clear();
        for (Figure child : childrenProperty()) {
            group.add(v.getNode(child));
        }
    }

    @Override
    public Node createNode(DrawingView drawingView) {
        return new Group();
    }
    public static HashMap<String, Key<?>> getFigureKeys() {
        try {
            HashMap<String, Key<?>> keys = Figure.getFigureKeys();
            for (Field f : GroupFigure.class.getDeclaredFields()) {
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
}
