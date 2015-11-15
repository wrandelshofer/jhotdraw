/* @(#)TextFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
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
import org.jhotdraw.draw.AbstractLeafFigure;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.key.SimpleFigureKey;
import org.jhotdraw.draw.TextHolderFigure;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.RenderContext;

/**
 * TextFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TextFigure extends AbstractLeafFigure implements StrokedShapeFigure, FilledShapeFigure, TextHolderFigure {

    /**
     * The CSS type selector for this object is {@code "Text"}.
     */
    public final static String TYPE_SELECTOR = "Text";

    public final static SimpleFigureKey<Point2D> ORIGIN = new SimpleFigureKey<>("origin", Point2D.class, false, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), new Point2D(0, 0));

    private Text textNode;

    public TextFigure() {
        this(0, 0, "");
    }

    public TextFigure(Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }

    public TextFigure(double x, double y, String text) {
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
        set(ORIGIN, new Point2D(x, y));
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        return new Text();
    }

    @Override
    public void updateNode(RenderContext drawingView, Node node) {
        Text tn = (Text) node;
        tn.setText(get(TEXT));
        tn.setX(get(ORIGIN).getX());
        tn.setY(get(ORIGIN).getY());
        tn.setBoundsType(TextBoundsType.VISUAL);
        applyTransformableFigureProperties(tn);
        applyTextHolderProperties(tn);
        applyStrokedShapeProperties(tn);
        applyFilledShapeProperties(tn);
        tn.applyCss();
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return new ChopRectangleConnector(this);
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
    
    @Override
    public void layout() {
        // empty
    }
    
    @Override
    public boolean isLayoutable() {
        return false;
    }
}
