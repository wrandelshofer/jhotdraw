/*
 * @(#)AbstractStraightLineConnectionWithMarkersFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.handle.LineConnectorHandle;
import org.jhotdraw8.draw.handle.LineOutlineHandle;
import org.jhotdraw8.draw.handle.MoveHandle;
import org.jhotdraw8.draw.handle.SelectionHandle;
import org.jhotdraw8.draw.locator.PointLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.List;

/**
 * AbstractStraightLineConnectionWithMarkersFigure draws a straight line from start to end.
 * <p>
 * A subclass can hardcode the markers, or can implement one or multiple "markerable" interfaces
 * that allow user-defineable markers: {@link MarkerStartableFigure}, {@link MarkerEndableFigure},
 * {@link MarkerSegmentableFigure}.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractStraightLineConnectionWithMarkersFigure extends AbstractLineConnectionFigure
        implements PathIterableFigure {

    public AbstractStraightLineConnectionWithMarkersFigure() {
        this(0, 0, 1, 1);
    }

    public AbstractStraightLineConnectionWithMarkersFigure(@NonNull Point2D start, @NonNull Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public AbstractStraightLineConnectionWithMarkersFigure(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
    }

    @Override
    public void createHandles(HandleType handleType, @NonNull List<Handle> list) {
        if (handleType == HandleType.SELECT) {
            list.add(new LineOutlineHandle(this));
        } else if (handleType == HandleType.MOVE) {
            list.add(new LineOutlineHandle(this));
            if (get(START_CONNECTOR) == null) {
                list.add(new MoveHandle(this, new PointLocator(START)));
            } else {
                list.add(new SelectionHandle(this, new PointLocator(START)));
            }
            if (get(END_CONNECTOR) == null) {
                list.add(new MoveHandle(this, new PointLocator(END)));
            } else {
                list.add(new SelectionHandle(this, new PointLocator(END)));
            }
        } else if (handleType == HandleType.RESIZE) {
            list.add(new LineOutlineHandle(this));
            list.add(new LineConnectorHandle(this, START, START_CONNECTOR, START_TARGET));
            list.add(new LineConnectorHandle(this, END, END_CONNECTOR, END_TARGET));
        } else if (handleType == HandleType.POINT) {
            list.add(new LineOutlineHandle(this));
            list.add(new LineConnectorHandle(this, START, START_CONNECTOR, START_TARGET));
            list.add(new LineConnectorHandle(this, END, END_CONNECTOR, END_TARGET));
        } else if (handleType == HandleType.TRANSFORM) {
            list.add(new LineOutlineHandle(this));
        } else {
            super.createHandles(handleType, list);
        }
    }

    @NonNull
    @Override
    public Node createNode(RenderContext drawingView) {
        javafx.scene.Group g = new javafx.scene.Group();
        final Line line = new Line();
        final Path startMarker = new Path();
        final Path endMarker = new Path();
        startMarker.setStroke(null);
        endMarker.setStroke(null);
        g.getChildren().addAll(line, startMarker, endMarker);
        return g;
    }

    public abstract double getMarkerCenterScaleFactor();

    @Nullable
    public abstract String getMarkerCenterShape();

    public abstract double getMarkerEndScaleFactor();

    @Nullable
    public abstract String getMarkerEndShape();

    public abstract double getMarkerStartScaleFactor();

    @Nullable
    public abstract String getMarkerStartShape();

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        // FIXME include markers in path
        return Shapes.awtShapeFromFX(new Line(
                getNonNull(START_X).getConvertedValue(),
                getNonNull(START_Y).getConvertedValue(),
                getNonNull(END_X).getConvertedValue(),
                getNonNull(END_Y).getConvertedValue())).getPathIterator(tx);
    }

    public abstract double getStrokeCutEnd(RenderContext ctx);

    public abstract double getStrokeCutStart(RenderContext ctx);

    @Override
    public void layout(@NonNull RenderContext ctx) {
        Point2D start = getNonNull(START).getConvertedValue();
        Point2D end = getNonNull(END).getConvertedValue();
        Connector startConnector = get(START_CONNECTOR);
        Connector endConnector = get(END_CONNECTOR);
        Figure startTarget = get(START_TARGET);
        Figure endTarget = get(END_TARGET);
        if (startConnector != null && startTarget != null) {
            start = startConnector.getPositionInWorld(this, startTarget);
        }
        if (endConnector != null && endTarget != null) {
            end = endConnector.getPositionInWorld(this, endTarget);
        }

        if (startConnector != null && startTarget != null) {
            java.awt.geom.Point2D.Double chp = startConnector.chopStart(ctx, this, startTarget, start, end).getPoint();
            final Point2D p = worldToParent(chp.getX(),chp.getY());
            set(START, new CssPoint2D(p));
        }
        if (endConnector != null && endTarget != null) {
            java.awt.geom.Point2D.Double chp = endConnector.chopEnd(ctx, this, endTarget, start, end).getPoint();
            final Point2D p = worldToParent(chp.getX(),chp.getY());
            set(END, new CssPoint2D(p));
        }
    }

    public void translateInLocal(@NonNull CssPoint2D t) {
        set(START, getNonNull(START).add(t));
        set(END, getNonNull(END).add(t));
    }

    /**
     * This method can be overridden by a subclass to apply styles to the marker
     * node.
     *
     * @param ctx  the context
     * @param node the node
     */
    protected void updateEndMarkerNode(RenderContext ctx, Path node) {
        // empty
    }

    /**
     * This method can be overridden by a subclass to apply styles to the line
     * node.
     *
     * @param ctx  the context
     * @param node the node
     */
    protected void updateLineNode(RenderContext ctx, Line node) {

    }

    protected void updateMarkerNode(RenderContext ctx, javafx.scene.Group group,
                                    @NonNull Path markerNode,
                                    @NonNull Point2D start, @NonNull Point2D end, @Nullable String svgString, double markerScaleFactor) {
        if (svgString != null) {
            markerNode.getElements().setAll(Shapes.fxPathElementsFromSvgString(svgString));
            double angle = Geom.atan2(start.getY() - end.getY(), start.getX() - end.getX());
            markerNode.getTransforms().setAll(
                    new Rotate(angle * 180 / Math.PI, start.getX(), start.getY()),
                    new Scale(markerScaleFactor, markerScaleFactor, start.getX(), start.getY()),
                    new Translate(start.getX(), start.getY()));
            markerNode.setVisible(true);
        } else {
            markerNode.setVisible(false);
        }
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        javafx.scene.Group g = (javafx.scene.Group) node;
        Line lineNode = (Line) g.getChildren().get(0);
        final Path startMarkerNode = (Path) g.getChildren().get(1);
        final Path endMarkerNode = (Path) g.getChildren().get(2);

        Point2D start = getNonNull(START).getConvertedValue();
        Point2D end = getNonNull(END).getConvertedValue();

        final double startInset = getStrokeCutStart(ctx);
        final double endInset = getStrokeCutEnd(ctx);
        final String startMarkerStr = getMarkerStartShape();
        final String endMarkerStr = getMarkerEndShape();

        Point2D dir = end.subtract(start).normalize();
        if (startInset != 0) {
            start = start.add(dir.multiply(startInset));
        }
        if (endInset != 0) {
            end = end.add(dir.multiply(-endInset));
        }
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());

        updateLineNode(ctx, lineNode);
        updateMarkerNode(ctx, g, startMarkerNode, start, end, startMarkerStr, getMarkerStartScaleFactor());
        updateMarkerNode(ctx, g, endMarkerNode, end, start, endMarkerStr, getMarkerEndScaleFactor());
        updateStartMarkerNode(ctx, startMarkerNode);
        updateEndMarkerNode(ctx, endMarkerNode);
    }

    /**
     * This method can be overridden by a subclass to apply styles to the marker
     * node.
     *
     * @param ctx  the context
     * @param node the node
     */
    protected void updateStartMarkerNode(RenderContext ctx, Path node) {
        // empty
    }
}
