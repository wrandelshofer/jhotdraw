/*
 * @(#)LineConnectionFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * A figure which draws a line connection between two figures.
 *
 * @author Werner Randelshofer
 */
public class LineConnectionFigure extends AbstractLineConnectionFigure
        implements StrokableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        PathIterableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "LineConnection";

    public LineConnectionFigure() {
        this(0, 0, 1, 1);
    }

    public LineConnectionFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public LineConnectionFigure(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
    }

    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Line();
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {

        Line lineNode = (Line) node;
        Point2D start = getNonnull(START).getConvertedValue();
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        Point2D end = getNonnull(END).getConvertedValue();
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());

        applyHideableFigureProperties(ctx, lineNode);
        applyStrokableFigureProperties(ctx, lineNode);
        applyCompositableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        return Shapes.awtShapeFromFX(new Line(
                getNonnull(START_X).getConvertedValue(),
                getNonnull(START_Y).getConvertedValue(),
                getNonnull(END_X).getConvertedValue(),
                getNonnull(END_Y).getConvertedValue())).getPathIterator(tx);
    }

}
