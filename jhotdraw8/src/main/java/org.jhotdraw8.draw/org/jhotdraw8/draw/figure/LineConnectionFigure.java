/*
 * @(#)LineConnectionFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import org.jhotdraw8.annotation.NonNull;
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

    public LineConnectionFigure(@NonNull Point2D start, @NonNull Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public LineConnectionFigure(double startX, double startY, double endX, double endY) {
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
        Point2D start = getNonNull(START).getConvertedValue();
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        Point2D end = getNonNull(END).getConvertedValue();
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
                getNonNull(START_X).getConvertedValue(),
                getNonNull(START_Y).getConvertedValue(),
                getNonNull(END_X).getConvertedValue(),
                getNonNull(END_Y).getConvertedValue())).getPathIterator(tx);
    }

}
