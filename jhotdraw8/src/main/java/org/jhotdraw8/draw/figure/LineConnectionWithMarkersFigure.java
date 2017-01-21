/* @(#)LineConnectionWithMarkersFigure.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.SvgPathStyleableFigureKey;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * LineConnectionWithMarkersFigure.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class LineConnectionWithMarkersFigure extends AbstractLineConnectionFigure
        implements StrokeableFigure, FillableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "LineConnectionWithMarkers";
    public final static SvgPathStyleableFigureKey START_MARKER = new SvgPathStyleableFigureKey("start-marker", DirtyMask.of(DirtyBits.NODE), "M0,0 L-10,5 -10,-5Z");
    public final static SvgPathStyleableFigureKey END_MARKER = new SvgPathStyleableFigureKey("end-marker", DirtyMask.of(DirtyBits.NODE), "M0,0 L-10,5 -10,-5Z");
    public final static DoubleStyleableFigureKey START_OFFSET = new DoubleStyleableFigureKey("start-offset", DirtyMask.of(DirtyBits.NODE), 0.0);
    public final static DoubleStyleableFigureKey END_OFFSET = new DoubleStyleableFigureKey("end-offset", DirtyMask.of(DirtyBits.NODE), 0.0);

    public LineConnectionWithMarkersFigure() {
        this(0, 0, 1, 1);
    }

    public LineConnectionWithMarkersFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public LineConnectionWithMarkersFigure(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        javafx.scene.Group g = new javafx.scene.Group();
        g.getProperties().put("line", new Line());
        g.getProperties().put("startMarker", new SVGPath());
        g.getProperties().put("endMarker", new SVGPath());
        g.getChildren().add((Line) (g.getProperties().get("line")));
        return g;
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        javafx.scene.Group g = (javafx.scene.Group) node;

        Line lineNode = (Line) g.getProperties().get("line");
        Point2D start = get(START);
        Point2D end = get(END);

        SVGPath startMarker = (SVGPath) g.getProperties().get("startMarker");
        SVGPath endMarker = (SVGPath) g.getProperties().get("endMarker");
        applyHideableFigureProperties(g);
        applyStrokeableFigureProperties(lineNode);
        applyCompositableFigureProperties(g);
        applyStyleableFigureProperties(ctx, node);
        applyStrokeColorProperties(startMarker);
        applyStrokeColorProperties(endMarker);
        applyStrokeWidthProperties(startMarker);
        applyStrokeWidthProperties(endMarker);
        applyStrokeCapAndJoinProperties(startMarker);
        applyStrokeCapAndJoinProperties(endMarker);
        applyFillableFigureProperties(startMarker);
        applyFillableFigureProperties(endMarker);

        final double startOffset = getStyled(START_OFFSET);
        final double endOffset = getStyled(END_OFFSET);
        double strokeWidth = getStyled(STROKE_WIDTH);
        String startMarkerP = getStyled(START_MARKER);
        if (startMarkerP != null) {
            startMarker.setContent(startMarkerP);
            double angle = Math.atan2(start.getY() - end.getY(), start.getX() - end.getX());
            startMarker.getTransforms().setAll(
                    new Rotate(angle * 180 / Math.PI, start.getX(), start.getY()),
                    //  new Scale(strokeWidth,strokeWidth,start.getX(),start.getY()),
                    new Translate(start.getX(), start.getY()));

            if (!g.getChildren().contains(startMarker)) {
                g.getChildren().add(startMarker);
            }
        } else {
            g.getChildren().remove(startMarker);
        }

        String endMarkerP = getStyled(END_MARKER);
        if (endMarkerP != null) {
            endMarker.setContent(endMarkerP);
            double angle = Math.atan2(end.getY() - start.getY(), end.getX() - start.getX());
            endMarker.getTransforms().setAll(
                    new Rotate(angle * 180 / Math.PI, end.getX(), end.getY()),
                    //new Scale(strokeWidth,strokeWidth,end.getX(),end.getY()),
                    new Translate(end.getX(), end.getY()));

            if (!g.getChildren().contains(endMarker)) {
                g.getChildren().add(endMarker);
            }
        }

        Point2D dir = end.subtract(start).normalize();
        if (startOffset != 0) {
            start = start.add(dir.multiply(startOffset));
        }
        if (endOffset != 0) {
            end = end.add(dir.multiply(-endOffset));
        }
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());

    }
}
