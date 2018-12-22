/* @(#)SimpleLineFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.transform.Transform;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.handle.LineOutlineHandle;
import org.jhotdraw8.draw.handle.MoveHandle;
import org.jhotdraw8.draw.handle.PointHandle;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.locator.PointLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Shapes;

import org.jhotdraw8.annotation.Nonnull;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.List;

/**
 * A figure which draws a straight line from a start point to an end point.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleLineFigure extends AbstractLeafFigure 
        implements StrokableFigure, HideableFigure, StyleableFigure, LockableFigure,
        CompositableFigure, TransformableFigure, PathIterableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Line";

    public final static CssSizeStyleableFigureKey START_X = new CssSizeStyleableFigureKey("startX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey START_Y = new CssSizeStyleableFigureKey("startY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS),  CssSize.ZERO);
    public final static CssSizeStyleableFigureKey END_X = new CssSizeStyleableFigureKey("endX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS),  CssSize.ZERO);
    public final static CssSizeStyleableFigureKey END_Y = new CssSizeStyleableFigureKey("endY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS),  CssSize.ZERO);
    public final static CssPoint2DStyleableMapAccessor START = new CssPoint2DStyleableMapAccessor("start", START_X, START_Y);
    public final static CssPoint2DStyleableMapAccessor END = new CssPoint2DStyleableMapAccessor("end", END_X, END_Y);

    public SimpleLineFigure() {
        this(0, 0, 1, 1);
    }

    public SimpleLineFigure(double startX, double startY, double endX, double endY) {
        set(START, new CssPoint2D(startX, startY));
        set(END, new CssPoint2D(endX, endY));
    }

    public SimpleLineFigure(Point2D start, Point2D end) {
        set(START, new CssPoint2D(start));
        set(END, new CssPoint2D(end));
    }

    @Nonnull
    @Override
    public Bounds getBoundsInLocal() {
        return getCssBoundsInLocal().getConvertedBoundsValue();
    }
    @Nonnull
    @Override
    public CssRectangle2D getCssBoundsInLocal() {
        CssPoint2D start = getNonnull(START);
        CssPoint2D end = getNonnull(END);
        return new CssRectangle2D(start,end);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        return Shapes.awtShapeFromFX(new Line(getNonnull(START_X).getConvertedValue(),
                getNonnull(START_Y).getConvertedValue(),
                getNonnull(END_X).getConvertedValue(),
                getNonnull(END_Y).getConvertedValue())).getPathIterator(tx);
    }

    @Override
    public void reshapeInLocal(@Nonnull Transform transform) {
        set(START, new CssPoint2D(transform.transform(getNonnull(START).getConvertedValue())));
        set(END, new CssPoint2D(transform.transform(getNonnull(END).getConvertedValue())));
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        set(START, new CssPoint2D(x, y));
        set(END, new CssPoint2D(x .add( width), y .add( height)));
    }

    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Line();
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        Line lineNode = (Line) node;
        applyHideableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyStrokableFigureProperties(ctx, lineNode);
        applyTransformableFigureProperties(ctx, node);
        applyCompositableFigureProperties(ctx, lineNode);
        Point2D start = getStyledNonnull(START).getConvertedValue();
        lineNode.setStartX(start.getX());
        lineNode.setStartY(start.getY());
        Point2D end = getStyledNonnull(END).getConvertedValue();
        lineNode.setEndX(end.getX());
        lineNode.setEndY(end.getY());
        lineNode.applyCss();
    }

    @Override
    public void createHandles(HandleType handleType, @Nonnull List<Handle> list) {
        if (handleType == HandleType.SELECT) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_SELECT_OUTLINE));
        } else if (handleType == HandleType.MOVE) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_MOVE_OUTLINE));
            list.add(new MoveHandle(this, new PointLocator(START), Handle.STYLECLASS_HANDLE_MOVE));
            list.add(new MoveHandle(this, new PointLocator(END), Handle.STYLECLASS_HANDLE_MOVE));
        } else if (handleType == HandleType.RESIZE) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_RESIZE_OUTLINE));
            list.add(new PointHandle(this, Handle.STYLECLASS_HANDLE_RESIZE, START));
            list.add(new PointHandle(this, Handle.STYLECLASS_HANDLE_RESIZE, END));
        } else if (handleType == HandleType.POINT) {
            list.add(new LineOutlineHandle(this, Handle.STYLECLASS_HANDLE_POINT_OUTLINE));
            list.add(new PointHandle(this, Handle.STYLECLASS_HANDLE_POINT, START));
            list.add(new PointHandle(this, Handle.STYLECLASS_HANDLE_POINT, END));
        } else {
            super.createHandles(handleType, list);
        }
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public void layout(RenderContext ctx) {
        // empty
    }

    @Override
    public boolean isLayoutable() {
        return false;
    }

}
