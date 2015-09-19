/* @(#)LineFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import static java.lang.Math.*;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Line;
import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.DirtyBits;
import org.jhotdraw.draw.DirtyMask;
import org.jhotdraw.draw.DrawingRenderer;
import org.jhotdraw.draw.FigureKey;
import org.jhotdraw.draw.connector.CenterConnector;
import org.jhotdraw.draw.connector.Connector;

/**
 * Renders a {@code javafx.scene.shape.Line}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineFigure extends AbstractShapeFigure {

    public final static FigureKey<Point2D> START = new FigureKey<>("start", Point2D.class, DirtyMask.of(DirtyBits.NODE,DirtyBits.GEOMETRY,DirtyBits.LAYOUT_BOUNDS,DirtyBits.VISUAL_BOUNDS), new Point2D(0, 0));
    public final static FigureKey<Point2D> END = new FigureKey<>("end", Point2D.class, DirtyMask.of(DirtyBits.NODE,DirtyBits.GEOMETRY,DirtyBits.LAYOUT_BOUNDS,DirtyBits.VISUAL_BOUNDS), new Point2D(0, 0));

    public LineFigure() {
        this(0, 0, 1, 1);
    }

    public LineFigure(double startX, double startY, double endX, double endY) {
        set(START, new Point2D(startX, startY));
        set(END, new Point2D(endX, endY));
    }

    public LineFigure(Point2D start, Point2D end) {
        set(START, start);
        set(END, end);
    }

    @Override
    public Bounds getBoundsInLocal() {
        Point2D start = get(START);
        Point2D end = get(END);
        return new BoundingBox(//
                min(start.getX(), end.getX()),//
                min(start.getY(), end.getY()),//
                abs(start.getX() - end.getX()), //
                abs(start.getY() - end.getY()));
    }

    @Override
    public void reshape(Transform transform) {
        set(START, transform.transform(get(START)));
        set(END, transform.transform(get(END)));
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        set(START, new Point2D(x, y));
        set(END, new Point2D(x + width, y + height));
    }

    @Override
    public Node createNode(DrawingRenderer drawingView) {
        return new Line();
    }

    @Override
    public void updateNode(DrawingRenderer drawingView, Node node) {
        Line lineNode = (Line) node;
        applyFigureProperties(lineNode);
        updateShapeProperties(lineNode);
        Point2D start = get(START);
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        Point2D end = get(END);
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());
    }
}
