/* @(#)LineConnectionWithMarkersFigure.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import javafx.css.StyleOrigin;
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
import org.jhotdraw8.draw.key.Scale2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.SvgPathStyleableFigureKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Shapes;

/**
 * LineConnectionWithMarkersFigure.
 *
 * @author Werner Randelshofer
 * @version $$Id: LineConnectionWithMarkersFigure.java 1330 2017-01-21 00:12:13Z
 * rawcoder $$
 */
public abstract class AbstractLineConnectionWithMarkersFigure extends AbstractLineConnectionFigure
        implements PathIterableFigure {

    // FIXME should not make this user-editable in this base class!
    public final static SvgPathStyleableFigureKey MARKER_START_SHAPE = new SvgPathStyleableFigureKey("marker-shape-start", DirtyMask.of(DirtyBits.NODE), null);
    public final static SvgPathStyleableFigureKey MARKER_END_SHAPE = new SvgPathStyleableFigureKey("marker-shape-end", DirtyMask.of(DirtyBits.NODE), null);
    //public final static SvgPathStyleableFigureKey MARKER_MIDDLE_SHAPE = new SvgPathStyleableFigureKey("marker-shape-mid", DirtyMask.of(DirtyBits.NODE), null);
    public final static DoubleStyleableFigureKey MARKER_LINE_INSET_START = new DoubleStyleableFigureKey("marker-line-inset-start", DirtyMask.of(DirtyBits.NODE), 0.0);
    public final static DoubleStyleableFigureKey MARKER_LINE_INSET_END = new DoubleStyleableFigureKey("marker-line-inset-end", DirtyMask.of(DirtyBits.NODE), 0.0);
    public final static DoubleStyleableFigureKey MARKER_SCALE_FACTOR_START = new DoubleStyleableFigureKey("marker-scale-factor-start", DirtyMask.of(DirtyBits.NODE), 1.0);
    public final static DoubleStyleableFigureKey MARKER_SCALE_FACTOR_END = new DoubleStyleableFigureKey("marker-scale-factor-end", DirtyMask.of(DirtyBits.NODE), 1.0);
    //public final static DoubleStyleableFigureKey MARKER_SCALE_FACTOR_MIDDLE = new DoubleStyleableFigureKey("marker-scale-factor-mid", DirtyMask.of(DirtyBits.NODE), 1.0);
    /**
     * Defines the line insets for start and end marker..
     */
    public static Scale2DStyleableMapAccessor MARKER_LINE_INSET = new Scale2DStyleableMapAccessor("marker-line-inset", MARKER_LINE_INSET_START, MARKER_LINE_INSET_END);
    /**
     * Defines the scale factor for start and end marker..
     */
    public static Scale2DStyleableMapAccessor MARKER_SCALE_FACTOR = new Scale2DStyleableMapAccessor("marker-scale-factor", MARKER_SCALE_FACTOR_START, MARKER_SCALE_FACTOR_END);
    //public static Scale3DStyleableMapAccessor MARKER_SCALE_FACTOR = new Scale3DStyleableMapAccessor("marker-scale-factor", MARKER_SCALE_FACTOR_START, MARKER_SCALE_FACTOR_END, MARKER_SCALE_FACTOR_MIDDLE);

    public AbstractLineConnectionWithMarkersFigure() {
        this(0, 0, 1, 1);
    }

    public AbstractLineConnectionWithMarkersFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public AbstractLineConnectionWithMarkersFigure(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        setStyled(StyleOrigin.USER_AGENT, MARKER_LINE_INSET_START, 10.0);
        setStyled(StyleOrigin.USER_AGENT, MARKER_LINE_INSET_END, 10.0);
        setStyled(StyleOrigin.USER_AGENT, MARKER_START_SHAPE, "M0,0 L-10,5 -10,-5Z");
        setStyled(StyleOrigin.USER_AGENT, MARKER_END_SHAPE, "M0,0 L-10,5 -10,-5Z");
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

    /**
     * This method can be overridden by a subclass to apply styles to the line
     * node.
     */
    protected void updateLineNode(RenderContext ctx, Line node) {

    }

    /**
     * This method can be overridden by a subclass to apply styles to the line
     * node.
     */
    protected void updateStartMarkerNode(RenderContext ctx, SVGPath node) {

    }

    /**
     * This method can be overridden by a subclass to apply styles to the line
     * node.
     */
    protected void updateEndMarkerNode(RenderContext ctx, SVGPath node) {

    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        javafx.scene.Group g = (javafx.scene.Group) node;
        Line lineNode = (Line) g.getProperties().get("line");
        final SVGPath startMarkerNode = (SVGPath) g.getProperties().get("startMarker");
        final SVGPath endMarkerNode = (SVGPath) g.getProperties().get("endMarker");

        Point2D start = get(START);
        Point2D end = get(END);

        final double startInset = getStyled(MARKER_LINE_INSET_START);
        final double endInset = getStyled(MARKER_LINE_INSET_END);
        final String startMarkerStr = getStyled(MARKER_START_SHAPE);

        updateMarkerNode(ctx, g, startMarkerNode, start, end, startMarkerStr, getStyled(MARKER_SCALE_FACTOR_START));
        final String endMarkerStr = getStyled(MARKER_END_SHAPE);
        updateMarkerNode(ctx, g, endMarkerNode, end, start, endMarkerStr, getStyled(MARKER_SCALE_FACTOR_END));

        Point2D dir = end.subtract(start).normalize();
        if (startInset != 0 && startMarkerStr != null) {
            start = start.add(dir.multiply(startInset * getStyled(MARKER_SCALE_FACTOR_START)));
        }
        if (endInset != 0 && endMarkerStr != null) {
            end = end.add(dir.multiply(-endInset * getStyled(MARKER_SCALE_FACTOR_END)));
        }
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());

        updateLineNode(ctx, lineNode);
        updateStartMarkerNode(ctx, startMarkerNode);
        updateEndMarkerNode(ctx, endMarkerNode);
    }

    private void updateMarkerNode(RenderContext ctx, javafx.scene.Group group,
            SVGPath markerNode,
            Point2D start, Point2D end, String svgString, double markerScaleFactor) {
        if (svgString != null) {
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

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        // FIXME include markers in path
        return Shapes.awtShapeFromFX(new Line(get(START_X), get(START_Y), get(END_X), get(END_Y))).getPathIterator(tx);
    }

}
