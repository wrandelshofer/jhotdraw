/* @(#)TextFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import org.jhotdraw.draw.FillableFigure;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.AbstractLeafFigure;
import org.jhotdraw.draw.CompositableFigure;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.HideableFigure;
import org.jhotdraw.draw.LockableFigure;
import org.jhotdraw.draw.TextableFigure;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.RenderContext;
import org.jhotdraw.draw.TransformableFigure;
import org.jhotdraw.draw.StrokeableFigure;
import org.jhotdraw.draw.StyleableFigure;
import org.jhotdraw.draw.TextHolderFigure;
import org.jhotdraw.draw.key.Point2DStyleableFigureKey;

/**
 * {@code TextFigure} is a {@code TextableFigure} which supports stroking
 * and filling of the text.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TextFigure extends AbstractLeafFigure implements StrokeableFigure, FillableFigure, TransformableFigure, TextableFigure, TextHolderFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for this object is {@code "Text"}.
     */
    public final static String TYPE_SELECTOR = "Text";
    public final static Point2DStyleableFigureKey ORIGIN = new Point2DStyleableFigureKey("origin", DirtyMask.of(DirtyBits.NODE,DirtyBits.LAYOUT,DirtyBits.CONNECTION_LAYOUT),new Point2D(0, 0));

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
        tn.setX(getStyled(ORIGIN).getX());
        tn.setY(getStyled(ORIGIN).getY());
        tn.setBoundsType(TextBoundsType.VISUAL);
        applyHideableFigureProperties(node);
        applyTransformableFigureProperties(tn);
        applyTextableFigureProperties(tn);
        applyStrokeableFigureProperties(tn);
        applyFillableFigureProperties(tn);
        applyCompositableFigureProperties(tn);
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
