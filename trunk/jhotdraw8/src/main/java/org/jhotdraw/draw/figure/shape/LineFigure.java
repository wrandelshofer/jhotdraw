/* @(#)LineFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure.shape;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import static java.lang.Math.*;
import java.util.List;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Line;
import org.jhotdraw.draw.AbstractLeafFigure;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.HideableFigure;
import org.jhotdraw.draw.LockableFigure;
import org.jhotdraw.draw.handle.HandleType;
import org.jhotdraw.draw.key.SimpleFigureKey;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.RenderContext;
import org.jhotdraw.draw.TransformableFigure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.LineOutlineHandle;
import org.jhotdraw.draw.handle.PointHandle;
import org.jhotdraw.draw.StrokeableFigure;
import org.jhotdraw.draw.StyleableFigure;
import org.jhotdraw.draw.handle.MoveHandleKit;
import org.jhotdraw.draw.handle.RotateHandle;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.Point2DStyleableFigureKey;
import org.jhotdraw.draw.key.Point2DStyleableMapAccessor;
import org.jhotdraw.draw.locator.PointLocator;

/**
 * Renders a {@code javafx.scene.shape.Line}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineFigure extends AbstractLeafFigure implements StrokeableFigure, HideableFigure, StyleableFigure, LockableFigure {

    /**
     * The CSS type selector for this object is {@code "Line"}.
     */
    public final static String TYPE_SELECTOR = "Line";

    public final static DoubleStyleableFigureKey START_X = new DoubleStyleableFigureKey("startX",   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey START_Y = new DoubleStyleableFigureKey("startY",   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey END_X = new DoubleStyleableFigureKey("endX",   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey END_Y = new DoubleStyleableFigureKey("endY",   DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);
    public final static Point2DStyleableMapAccessor START = new Point2DStyleableMapAccessor("start", START_X,START_Y);
    public final static Point2DStyleableMapAccessor END = new Point2DStyleableMapAccessor("end", END_X,END_Y);

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
        applyHideableFigureProperties(node);
        //applyTransformableFigureProperties(lineNode);
        applyStrokeableFigureProperties(lineNode);
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
    public void createHandles(HandleType handleType, DrawingView dv, List<Handle> list) {
        if (handleType == HandleType.SELECT) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_SELECT_OUTLINE));
        } else if (handleType == HandleType.MOVE) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_MOVE_OUTLINE));
            list.add(new MoveHandleKit.MoveHandle(this, Handle.STYLECLASS_HANDLE_MOVE, new PointLocator( START)));
            list.add(new MoveHandleKit.MoveHandle(this, Handle.STYLECLASS_HANDLE_MOVE, new PointLocator( END)));
        } else if (handleType == HandleType.RESIZE) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_RESIZE_OUTLINE));
            list.add(new PointHandle(this, Handle.STYLECLASS_HANDLE_RESIZE, START));
            list.add(new PointHandle(this, Handle.STYLECLASS_HANDLE_RESIZE, END));
            //list.add(new RotateHandle(this, Handle.STYLECLASS_HANDLE_ROTATE));
        }else{
            super.createHandles(handleType, dv, list);
        }
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
