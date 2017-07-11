/* @(#)LineConnectionWithMarkersFigure.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.jhotdraw8.draw.key.CssColor;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * LineConnectionWithMarkersFigure.
 *
 * @author Werner Randelshofer
 * @version $$Id: LineConnectionWithMarkersFigure.java 1330 2017-01-21 00:12:13Z
 * rawcoder $$
 */
public class LineConnectionWithMarkersFigure extends AbstractLineConnectionWithMarkersFigure
        implements HideableFigure, StyleableFigure, 
        LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "LineConnectionWithMarkers";

    public LineConnectionWithMarkersFigure() {
        this(0, 0, 1, 1);
    }

    public LineConnectionWithMarkersFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public LineConnectionWithMarkersFigure(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        set(FILL, new CssColor("black", Color.BLACK));
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        super.updateNode(ctx,node);

        applyHideableFigureProperties(node);
        applyCompositableFigureProperties(node);
        applyStyleableFigureProperties(ctx, node);
    }
}
