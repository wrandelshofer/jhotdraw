/* @(#)SimpleLineConnectionWithMarkersFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import org.jhotdraw8.draw.key.CssColor;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * SimpleLineConnectionWithMarkersFigure.
 *
 * @author Werner Randelshofer
 * @version $$Id: SimpleLineConnectionWithMarkersFigure.java 1330 2017-01-21 00:12:13Z
 rawcoder $$
 */
public class SimpleLineConnectionWithMarkersFigure extends AbstractLineConnectionWithMarkersFigure
        implements HideableFigure, StyleableFigure, 
        LockableFigure, CompositableFigure, FillableFigure, StrokeableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "LineConnectionWithMarkers";

    public SimpleLineConnectionWithMarkersFigure() {
        this(0, 0, 1, 1);
    }

    public SimpleLineConnectionWithMarkersFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public SimpleLineConnectionWithMarkersFigure(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        set(FILL, new CssColor("black", Color.BLACK));
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    protected void updateEndMarkerNode(RenderContext ctx, SVGPath node) {
        super.updateEndMarkerNode(ctx, node); 
        applyFillableFigureProperties(node);
    }

    @Override
    protected void updateLineNode(RenderContext ctx, Line node) {
        super.updateLineNode(ctx, node); 
        applyStrokeableFigureProperties(node);
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        super.updateNode(ctx,node);

        applyHideableFigureProperties(node);
        applyCompositableFigureProperties(node);
        applyStyleableFigureProperties(ctx, node);
    }

    @Override
    protected void updateStartMarkerNode(RenderContext ctx, SVGPath node) {
        super.updateStartMarkerNode(ctx, node); 
        applyFillableFigureProperties(node);
    }    
}
