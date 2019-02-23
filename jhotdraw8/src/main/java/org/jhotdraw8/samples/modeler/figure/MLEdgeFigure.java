/* @(#)MLEdgeFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.draw.figure.AbstractLineConnectionWithMarkersFigure;
import org.jhotdraw8.draw.figure.CompositableFigure;
import org.jhotdraw8.draw.figure.EndMarkerableFigure;
import org.jhotdraw8.draw.figure.HideableFigure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.MarkerFillableFigure;
import org.jhotdraw8.draw.figure.MidMarkerableFigure;
import org.jhotdraw8.draw.figure.StartMarkerableFigure;
import org.jhotdraw8.draw.figure.StrokableFigure;
import org.jhotdraw8.draw.figure.StrokeCuttableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * Renders a "UMLEdge" element.
 * <p>
 * A UMLEdge is drawn as a line that connects two "UMLShape" figures, such
 * as two "UMLClassifierShape" figures.<br>
 * An UMLEdge can have start-, mid- and end-markers which help to visualize the
 * metaclass and direction of the edge.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MLEdgeFigure extends AbstractLineConnectionWithMarkersFigure
        implements HideableFigure, StyleableFigure,
        LockableFigure, CompositableFigure, MarkerFillableFigure, StrokableFigure,
        StartMarkerableFigure, EndMarkerableFigure, MidMarkerableFigure, StrokeCuttableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "MLEdge";

    public MLEdgeFigure() {
        this(0, 0, 1, 1);
    }

    public MLEdgeFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public MLEdgeFigure(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        set(MARKER_FILL, new CssColor("black", Color.BLACK));
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    protected void updateEndMarkerNode(RenderContext ctx, @Nonnull Path node) {
        super.updateEndMarkerNode(ctx, node);
        applyMarkerFillableFigureProperties(node);
    }

    @Override
    protected void updateLineNode(RenderContext ctx, @Nonnull Line node) {
        super.updateLineNode(ctx, node);
        applyStrokableFigureProperties(ctx, node);
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        super.updateNode(ctx, node);

        applyHideableFigureProperties(ctx, node);
        applyCompositableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
    }

    @Override
    protected void updateStartMarkerNode(RenderContext ctx, @Nonnull Path node) {
        super.updateStartMarkerNode(ctx, node);
        applyMarkerFillableFigureProperties(node);
    }

    @Override
    public double getMarkerEndScaleFactor() {
        return getStyledNonnull(EndMarkerableFigure.MARKER_END_SCALE_FACTOR);
    }

    @Override
    public String getMarkerEndShape() {
        return getStyled(EndMarkerableFigure.MARKER_END_SHAPE);
    }

    @Override
    public double getMarkerStartScaleFactor() {
        return getStyledNonnull(StartMarkerableFigure.MARKER_START_SCALE_FACTOR);
    }

    @Override
    public String getMarkerStartShape() {
        return getStyled(StartMarkerableFigure.MARKER_START_SHAPE);
    }

    @Override
    public double getStrokeCutEnd(RenderContext ctx) {
        return StrokeCuttableFigure.super.getStrokeCutEnd();
    }

    @Override
    public double getStrokeCutStart(RenderContext ctx) {
        return StrokeCuttableFigure.super.getStrokeCutStart();
    }
}
