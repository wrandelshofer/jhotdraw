/* @(#)SimpleLineConnectionFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Shapes;

/**
 * A figure which draws a line connection between two figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleLineConnectionFigure extends AbstractLineConnectionFigure
        implements StrokeableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        PathIterableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "LineConnection";

    public SimpleLineConnectionFigure() {
        this(0, 0, 1, 1);
    }

    public SimpleLineConnectionFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public SimpleLineConnectionFigure(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
    }

    @NonNull
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Line();
    }

    @NonNull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {

        Line lineNode = (Line) node;
        Point2D start = get(START);
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        Point2D end = get(END);
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());

        applyHideableFigureProperties(lineNode);
        applyStrokeableFigureProperties(lineNode);
        applyCompositableFigureProperties(node);
        applyStyleableFigureProperties(ctx, node);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        return Shapes.awtShapeFromFX(new Line(get(START_X), get(START_Y), get(END_X), get(END_Y))).getPathIterator(tx);
    }

}
