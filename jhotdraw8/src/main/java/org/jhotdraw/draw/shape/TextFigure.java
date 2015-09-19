/* @(#)TextFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.DirtyBits;
import org.jhotdraw.draw.DirtyMask;
import org.jhotdraw.draw.DrawingRenderer;
import org.jhotdraw.draw.FigureKey;
import org.jhotdraw.draw.TextHolderFigure;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;

/**
 * TextFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TextFigure extends AbstractConnectableShapeFigure implements TextHolderFigure {
     public final static FigureKey<Point2D> ORIGIN = new FigureKey<>("origin", Point2D.class, DirtyMask.of(DirtyBits.NODE,DirtyBits.GEOMETRY,DirtyBits.LAYOUT_BOUNDS,DirtyBits.VISUAL_BOUNDS), new Point2D(0, 0));

    private Text textNode;

    public TextFigure() {
        this(0, 0, "");
    }

    public TextFigure(Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }

    public TextFigure(double x, double y,String text) {
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
    public void reshape(double x, double y, double width, double height) {
        set(ORIGIN,new Point2D(x,y));
    }

    @Override
    public Node createNode(DrawingRenderer drawingView) {
        return new Text();
    }

    @Override
    public void updateNode(DrawingRenderer drawingView, Node node) {
        Text tn = (Text) node;
        tn.setText(get(TEXT));
        tn.setX(get(ORIGIN).getX());
        tn.setY(get(ORIGIN).getY());
        tn.setBoundsType(TextBoundsType.VISUAL);
        updateTextProperties(tn);
    }
    @Override
    public Connector findConnector(Point2D p, ConnectionFigure prototype) {
        return new ChopRectangleConnector();
    }
}
