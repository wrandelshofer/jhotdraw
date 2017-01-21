/* @(#)LineConnectionWithMarkersFigure.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
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
import org.jhotdraw8.text.CssColor;

/**
 * LineConnectionWithMarkersFigure.
 *
 * @author Werner Randelshofer
 * @version $$Id: LineConnectionWithMarkersFigure.java 1330 2017-01-21 00:12:13Z
 * rawcoder $$
 */
public class LineConnectionWithMarkersFigure extends AbstractLineConnectionFigure
        implements StrokeableFigure, FillableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "LineConnectionWithMarkers";
    public final static SvgPathStyleableFigureKey START_MARKER = new SvgPathStyleableFigureKey("marker-shape-start", DirtyMask.of(DirtyBits.NODE), "M0,0 L-10,5 -10,-5Z");
    public final static SvgPathStyleableFigureKey END_MARKER = new SvgPathStyleableFigureKey("marker-shape-end", DirtyMask.of(DirtyBits.NODE), "M0,0 L-10,5 -10,-5Z");
    public final static DoubleStyleableFigureKey START_MARKER_LINE_INSET = new DoubleStyleableFigureKey("marker-line-inset-start", DirtyMask.of(DirtyBits.NODE), 10.0);
    public final static DoubleStyleableFigureKey END_MARKER_LINE_INSET = new DoubleStyleableFigureKey("marker-line-inset-end", DirtyMask.of(DirtyBits.NODE), 10.0);
    public final static DoubleStyleableFigureKey START_MARKER_SCALE_FACTOR = new DoubleStyleableFigureKey("marker-scale-factor-start", DirtyMask.of(DirtyBits.NODE), 1.0);
    public final static DoubleStyleableFigureKey END_MARKER_SCALE_FACTOR = new DoubleStyleableFigureKey("marker-scale-factor-end", DirtyMask.of(DirtyBits.NODE), 1.0);

    public LineConnectionWithMarkersFigure() {
        this(0, 0, 1, 1);
    }

    public LineConnectionWithMarkersFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public LineConnectionWithMarkersFigure(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        set(FILL_COLOR, new CssColor("black", Color.BLACK));
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

        applyHideableFigureProperties(g);
        applyStrokeableFigureProperties(lineNode);
        applyCompositableFigureProperties(g);
        applyStyleableFigureProperties(ctx, node);

        final double startInset = getStyled(START_MARKER_LINE_INSET);
        final double endInset = getStyled(END_MARKER_LINE_INSET);
        final String startMarkerStr = getStyled(START_MARKER);

        updateMarkerNode(ctx, g, (SVGPath) g.getProperties().get("startMarker"), start, end, startMarkerStr, getStyled(START_MARKER_SCALE_FACTOR));
        final String endMarkerStr = getStyled(END_MARKER);
        updateMarkerNode(ctx, g, (SVGPath) g.getProperties().get("endMarker"), end, start, endMarkerStr, getStyled(END_MARKER_SCALE_FACTOR));

        Point2D dir = end.subtract(start).normalize();
        if (startInset != 0 && startMarkerStr!=null) {
            start = start.add(dir.multiply(startInset * getStyled(START_MARKER_SCALE_FACTOR)));
        }
        if (endInset != 0&&endMarkerStr!=null) {
            end = end.add(dir.multiply(-endInset * getStyled(END_MARKER_SCALE_FACTOR)));
        }
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());
    }

    private void updateMarkerNode(RenderContext ctx, javafx.scene.Group group,
            SVGPath markerNode,
            Point2D start, Point2D end, String svgString, double markerScaleFactor) {
        if (svgString != null) {
            applyFillableFigureProperties(markerNode);
            markerNode.setContent(svgString);
            double angle = Math.atan2(start.getY() - end.getY(), start.getX() - end.getX());
            markerNode.getTransforms().setAll(
                    new Rotate(angle * 180 / Math.PI, start.getX(), start.getY()),
                    new Scale(markerScaleFactor, markerScaleFactor, start.getX(), start.getY()),
                    new Translate(start.getX(), start.getY()));

            if (!group.getChildren().contains(markerNode)) {
                group.getChildren().add(markerNode);
            }
        } else {
            group.getChildren().remove(markerNode);
        }

    }

}
