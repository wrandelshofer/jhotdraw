/* @(#)LineConnectionFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.SimpleFigureKey;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.connector.Connector;
import static java.lang.Math.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.connector.ChopRectangleConnector;
import org.jhotdraw8.draw.handle.BoundsInLocalOutlineHandle;
import org.jhotdraw8.draw.handle.BoundsInTransformOutlineHandle;
import org.jhotdraw8.draw.handle.ConnectionPointHandle;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.LineOutlineHandle;
import org.jhotdraw8.draw.handle.MoveHandle;
import org.jhotdraw8.draw.handle.RotateHandle;
import org.jhotdraw8.draw.handle.TransformHandleKit;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.Point2DStyleableMapAccessor;
import org.jhotdraw8.draw.locator.PointLocator;

/**
 * A figure which draws a line connection between two figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineConnectionFigure extends AbstractLineConnectionFigure
        implements StrokeableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "LineConnection";

    public LineConnectionFigure() {
        this(0, 0, 1, 1);
    }

    public LineConnectionFigure(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public LineConnectionFigure(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        super.updateNode(ctx, node);
        Line lineNode = (Line) node;
        applyHideableFigureProperties(lineNode);
        applyStrokeableFigureProperties(lineNode);
        applyCompositableFigureProperties(node);
        applyStyleableFigureProperties(ctx, node);
    }
}
