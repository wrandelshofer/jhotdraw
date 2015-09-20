/* @(#)TextFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.draw.shape.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.connector.Connector;

/**
 * TextFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LabelFigure extends AbstractLeafFigure implements TextHolderFigure {

    public final static Key<Point2D> ORIGIN = new Key<>("origin", Point2D.class, new Point2D(0, 0));

    private ReadOnlyObjectWrapper<Bounds> layoutBounds = null;

    private Text textNode;

    public LabelFigure() {
        this(0, 0, "");
    }

    public LabelFigure(Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }

    public LabelFigure(double x, double y,String text) {
        set(TEXT, text);
        set(ORIGIN, new Point2D(x, y));
    }

    @Override
    public Bounds getBoundsInLocal() {
        if (textNode == null) {
            textNode = new Text();
        }
        updateNode(null, textNode);

        Bounds b = textNode.getLayoutBounds();
        return new BoundingBox(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshape(Transform transform) {
        Point2D o = get(ORIGIN);
        o = transform.transform(o);
        set(ORIGIN, o);
    }

    @Override
    public Node createNode(DrawingRenderer drawingView) {
        return new Text();
    }

    @Override
    public void updateNode(DrawingRenderer drawingView, Node node) {
        Text textNode = (Text) node;
        textNode.setText(get(TEXT));
        textNode.setX(get(ORIGIN).getX());
        textNode.setY(get(ORIGIN).getY());
        textNode.setBoundsType(TextBoundsType.VISUAL);
        updateTextProperties(textNode);
    }

    @Override
    public boolean isLayoutable() {
        return false;
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return null;
    }

    @Override
    public void layout() {
        // empty!
    }
}
