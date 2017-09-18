/* @(#)LineConnectionWithMarkersFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Shapes;

/**
 * LineConnectionWithMarkersFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractLineConnectionWithMarkersFigure extends AbstractLineConnectionFigure
        implements PathIterableFigure {

    public AbstractLineConnectionWithMarkersFigure() {
        this(0, 0, 1, 1);
    }

    public AbstractLineConnectionWithMarkersFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public AbstractLineConnectionWithMarkersFigure(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
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
     *
     * @param ctx the context
     * @param node the node
     */
    protected void updateLineNode(RenderContext ctx, Line node) {

    }

    /**
     * This method can be overridden by a subclass to apply styles to the marker
     * node.
     *
     * @param ctx the context
     * @param node the node
     */
    protected void updateStartMarkerNode(RenderContext ctx, SVGPath node) {

    }

    /**
     * This method can be overridden by a subclass to apply styles to the marker
     * node.
     *
     * @param ctx the context
     * @param node the node
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

        final double startInset = getStrokeCutStart();
        final double endInset = getStrokeCutEnd();
        final String startMarkerStr = getMarkerStartShape();
        updateMarkerNode(ctx, g, startMarkerNode, start, end, startMarkerStr, getMarkerStartScaleFactor());
        final String endMarkerStr = getMarkerEndShape();
        updateMarkerNode(ctx, g, endMarkerNode, end, start, endMarkerStr, getMarkerEndScaleFactor());

        Point2D dir = end.subtract(start).normalize();
        if (startInset != 0 && startMarkerStr != null) {
            start = start.add(dir.multiply(startInset * getMarkerStartScaleFactor()));
        }
        if (endInset != 0 && endMarkerStr != null) {
            end = end.add(dir.multiply(-endInset * getMarkerEndScaleFactor()));
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

    public abstract double getStrokeCutStart();

    public abstract double getStrokeCutEnd();

    public abstract String getMarkerStartShape();

    public abstract double getMarkerStartScaleFactor();

    public abstract String getMarkerEndShape();

    public abstract double getMarkerEndScaleFactor();

}
