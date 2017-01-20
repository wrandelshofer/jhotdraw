/* @(#)LineFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Polygon;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.PolyPointEditHandle;
import org.jhotdraw8.draw.handle.PolyPointMoveHandle;
import org.jhotdraw8.draw.handle.PolygonOutlineHandle;
import org.jhotdraw8.draw.key.Point2DListStyleableFigureKey;

/**
 * A figure which draws a closed polygon.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PolygonFigure extends AbstractLeafFigure implements StrokeableFigure, FillableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure, TransformableFigure, ResizableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Polygon";

    public final static Point2DListStyleableFigureKey POINTS = PolylineFigure.POINTS;

    public PolygonFigure() {
        this(0, 0, 1, 1);
    }

    public PolygonFigure(double startX, double startY, double endX, double endY) {
        set(POINTS, ImmutableObservableList.of(new Point2D(startX, startY), new Point2D(endX, endY)));
    }

    public PolygonFigure(Point2D... points) {
        set(POINTS, ImmutableObservableList.of(points));
    }

    @Override
    public Bounds getBoundsInLocal() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (Point2D p : get(POINTS)) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
        }
        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public void reshapeInLocal(Transform transform) {
        ArrayList<Point2D> newP = new ArrayList<>(get(POINTS));
        for (int i = 0, n = newP.size(); i < n; i++) {
            newP.set(i, transform.transform(newP.get(i)));
        }
        set(POINTS, new ImmutableObservableList<>(newP));
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        return new Polygon();
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Polygon lineNode = (Polygon) node;
        applyHideableFigureProperties(node);
        applyFillableFigureProperties(lineNode);
        applyStyleableFigureProperties(ctx, node);
        applyStrokeableFigureProperties(lineNode);
        applyTransformableFigureProperties(node);
        applyCompositableFigureProperties(lineNode);
        final ImmutableObservableList<Point2D> points = getStyled(POINTS);
        List<Double> list = new ArrayList<>(points.size() * 2);
        for (Point2D p : points) {
            list.add(p.getX());
            list.add(p.getY());
        }
        lineNode.getPoints().setAll(list);
        lineNode.applyCss();
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return null;
    }

    @Override
    public void createHandles(HandleType handleType, List<Handle> list) {
        if (handleType == HandleType.SELECT) {
            list.add(new PolygonOutlineHandle(this, null, Handle.STYLECLASS_HANDLE_SELECT_OUTLINE));
        } else if (handleType == HandleType.MOVE) {
            list.add(new PolygonOutlineHandle(this, null, Handle.STYLECLASS_HANDLE_MOVE_OUTLINE));
            for (int i = 0, n = get(POINTS).size(); i < n; i++) {
                list.add(new PolyPointMoveHandle(this, POINTS, i, Handle.STYLECLASS_HANDLE_MOVE));
            }
        } else if (handleType == HandleType.POINT) {
            list.add(new PolygonOutlineHandle(this, POINTS, Handle.STYLECLASS_HANDLE_POINT_OUTLINE));
            for (int i = 0, n = get(POINTS).size(); i < n; i++) {
                list.add(new PolyPointEditHandle(this, POINTS, i, Handle.STYLECLASS_HANDLE_POINT));
            }
        } else {
            super.createHandles(handleType, list);
        }
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }



    public static double[] toPointArray(Figure f) {
        List<Point2D> points = f.get(POINTS);
        double[] a = new double[points.size() * 2];
        for (int i = 0, n = points.size(), j = 0; i < n; i++, j += 2) {
            Point2D p = points.get(i);
            a[j] = p.getX();
            a[j + 1] = p.getY();
        }
        return a;
    }

}
