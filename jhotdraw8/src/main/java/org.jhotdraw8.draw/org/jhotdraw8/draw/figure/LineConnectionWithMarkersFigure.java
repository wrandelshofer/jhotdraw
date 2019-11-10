/*
 * @(#)LineConnectionWithMarkersFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * LineConnectionWithMarkersFigure.
 *
 * @author Werner Randelshofer
 */
public class LineConnectionWithMarkersFigure extends AbstractStraightLineConnectionWithMarkersFigure
        implements HideableFigure, StyleableFigure,
        LockableFigure, CompositableFigure, MarkerFillableFigure, StrokableFigure, MarkerStartableFigure,
        MarkerEndableFigure, StrokeCuttableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "LineConnectionWithMarkers";

    public LineConnectionWithMarkersFigure() {
        this(0, 0, 1, 1);
    }

    public LineConnectionWithMarkersFigure(@NonNull Point2D start, @NonNull Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public LineConnectionWithMarkersFigure(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        set(MARKER_FILL, new CssColor("black", Color.BLACK));
    }

    @NonNull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    protected void updateEndMarkerNode(RenderContext ctx, @NonNull Path node) {
        super.updateEndMarkerNode(ctx, node);
        applyMarkerFillableFigureProperties(ctx, node);
    }

    @Override
    protected void updateLineNode(RenderContext ctx, @NonNull Line node) {
        super.updateLineNode(ctx, node);
        applyStrokableFigureProperties(ctx, node);
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        super.updateNode(ctx, node);

        applyHideableFigureProperties(ctx, node);
        applyCompositableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
    }

    @Override
    protected void updateStartMarkerNode(RenderContext ctx, @NonNull Path node) {
        super.updateStartMarkerNode(ctx, node);
        applyMarkerFillableFigureProperties(ctx, node);
    }

    @Override
    public double getMarkerEndScaleFactor() {
        return getStyledNonNull(MarkerEndableFigure.MARKER_END_SCALE_FACTOR);
    }

    @Override
    public String getMarkerEndShape() {
        return getStyled(MarkerEndableFigure.MARKER_END_SHAPE);
    }

    @Override
    public double getMarkerStartScaleFactor() {
        return getStyledNonNull(MarkerStartableFigure.MARKER_START_SCALE_FACTOR);
    }

    @Override
    public String getMarkerStartShape() {
        return getStyled(MarkerStartableFigure.MARKER_START_SHAPE);
    }

    @Override
    public double getStrokeCutEnd(RenderContext ctx) {
        return StrokeCuttableFigure.super.getStrokeCutEnd();
    }

    @Override
    public double getStrokeCutStart(RenderContext ctx) {
        return StrokeCuttableFigure.super.getStrokeCutStart();
    }

    @Override
    public String getMarkerCenterShape() {
        return null;
    }

    @Override
    public double getMarkerCenterScaleFactor() {
        return 1.0;
    }
}
