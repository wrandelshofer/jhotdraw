/* @(#)LineFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.shape;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Line;
import org.jhotdraw.draw.DirtyBits;
import org.jhotdraw.draw.DirtyMask;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.SimpleFigureKey;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.RenderContext;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.LineWireframeHandle;
import org.jhotdraw.draw.handle.PointHandle;

/**
 * Renders a {@code javafx.scene.shape.Line}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineFigure extends AbstractShapeFigure {

    /**
     * The CSS type selector for this object is {@code "Line"}.
     */
    public final static String TYPE_SELECTOR = "Line";

    public final static SimpleFigureKey<Point2D> START = new SimpleFigureKey<>("start", Point2D.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), new Point2D(0, 0));
    public final static SimpleFigureKey<Point2D> END = new SimpleFigureKey<>("end", Point2D.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), new Point2D(0, 0));

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
    public Node createNode(RenderContext drawingView) {
        return new Line();
    }

    @Override
    public void updateNode(RenderContext drawingView, Node node) {
        Line lineNode = (Line) node;
        applyFigureProperties(lineNode);
        applyShapeProperties(lineNode);
        Point2D start = get(START);
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        Point2D end = get(END);
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());
        lineNode.applyCss();
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return null;
    }

    @Override
    public List<Handle> createHandles(int detailLevel, DrawingView dv) {
        ArrayList<Handle> list = new ArrayList<>();
        list.add(new LineWireframeHandle(this, dv, Handle.STYLECLASS_HANDLE_OUTLINE));
        list.add(new PointHandle(this, dv, Handle.STYLECLASS_HANDLE_POINT, START));
        list.add(new PointHandle(this, dv, Handle.STYLECLASS_HANDLE_POINT, END));
        return list;
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

}
