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
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.connector.Connector;

/**
 * GroupFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GroupFigure extends AbstractCompositeFigure {

    @Override
    public void reshape(Transform transform) {
        for (Figure child : childrenProperty()) {
            child.reshape(transform);
        }
    }

    @Override
    public void updateNode(DrawingRenderer v, Node n) {
        applyFigureProperties(n);
        ObservableList<Node> group = ((Group) n).getChildren();
        group.clear();
        for (Figure child : childrenProperty()) {
            group.add(v.getNode(child));
        }
    }

    @Override
    public Node createNode(DrawingRenderer drawingView) {
        return new Group();
    }

    @Override
    public boolean isLayoutable() {
        return false;
    }
}
