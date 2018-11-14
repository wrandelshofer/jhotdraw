/* @(#)SimpleLineConnectionFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javax.annotation.Nonnull;
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

        applyHideableFigureProperties(lineNode);
        applyStrokableFigureProperties(lineNode);
        applyCompositableFigureProperties(node);
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
