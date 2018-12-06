/* @(#)LineConnectionWithMarkersFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        javafx.scene.Group g = new javafx.scene.Group();
        final Line line = new Line();
        final Path startMarker=new Path();
        final Path endMarker=new Path();
        g.getChildren().addAll(line,startMarker,endMarker);
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
    protected void updateStartMarkerNode(RenderContext ctx, Path node) {
            // empty
    }

    /**
     * This method can be overridden by a subclass to apply styles to the marker
     * node.
     *
     * @param ctx the context
     * @param node the node
     */
    protected void updateEndMarkerNode(RenderContext ctx, Path node) {
            // empty
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        javafx.scene.Group g = (javafx.scene.Group) node;
        Line lineNode = (Line) g.getChildren().get(0);
        final Path startMarkerNode = (Path) g.getChildren().get(1);
        final Path endMarkerNode = (Path) g.getChildren().get(2);

        Point2D start = getNonnull(START).getConvertedValue();
        Point2D end = getNonnull(END).getConvertedValue();

        final double startInset = getStrokeCutStart();
        final double endInset = getStrokeCutEnd();
        final String startMarkerStr = getMarkerStartShape();
        updateMarkerNode(ctx, g, startMarkerNode, start, end, startMarkerStr, getMarkerStartScaleFactor());
        final String endMarkerStr = getMarkerEndShape();
        updateMarkerNode(ctx, g, endMarkerNode, end, start, endMarkerStr, getMarkerEndScaleFactor());

        Point2D dir = end.subtract(start).normalize();
        if (startInset != 0 && startMarkerStr != null) {
            start = start.add(dir.multiply(startInset));
        }
        if (endInset != 0 && endMarkerStr != null) {
            end = end.add(dir.multiply(-endInset));
        }
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());

        updateLineNode(ctx, lineNode);
        updateStartMarkerNode(ctx, startMarkerNode);
        updateEndMarkerNode(ctx, endMarkerNode);
    }

    protected void updateMarkerNode(RenderContext ctx, javafx.scene.Group group,
                                    @Nonnull Path markerNode,
                                    @Nonnull Point2D start, @Nonnull Point2D end, @Nullable String svgString, double markerScaleFactor) {
        if (svgString != null) {
            markerNode.getElements().setAll(Shapes.fxPathElementsFromSvgString(svgString));
            double angle = Math.atan2(start.getY() - end.getY(), start.getX() - end.getX());
            markerNode.getTransforms().setAll(
                    new Rotate(angle * 180 / Math.PI, start.getX(), start.getY()),
                    new Scale(markerScaleFactor, markerScaleFactor, start.getX(), start.getY()),
                    new Translate(start.getX(), start.getY()));
            markerNode.setVisible(true);
        }else{
            markerNode.setVisible(false);
        }
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        // FIXME include markers in path
        return Shapes.awtShapeFromFX(new Line(
                getNonnull(START_X).getConvertedValue(),
                getNonnull(START_Y).getConvertedValue(),
                getNonnull(END_X).getConvertedValue(),
                getNonnull(END_Y).getConvertedValue())).getPathIterator(tx);
    }

    public abstract double getStrokeCutStart();

    public abstract double getStrokeCutEnd();

    public abstract String getMarkerStartShape();

    public abstract double getMarkerStartScaleFactor();

    public abstract String getMarkerEndShape();

    public abstract double getMarkerEndScaleFactor();

}
