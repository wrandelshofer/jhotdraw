/* @(#)RectangleFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.util.List;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javax.annotation.Nonnull;

import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.handle.BoundsInLocalOutlineHandle;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.handle.RelativePointHandle;
import org.jhotdraw8.draw.handle.ResizeHandleKit;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssRectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;

/**
 * This is a special figure which is used to segment a drawing into tiles, when
 * exporting it using the {@link BitmapExportOutputFormat}.
 * <p>
 * This figure renders only with rendering intent
 * {@link RenderingIntent#EDITOR}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleSliceFigure extends AbstractLeafFigure implements Slice, TransformableFigure, ResizableFigure, HideableFigure, LockableFigure, StyleableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Slice";

    public final static CssSizeStyleableFigureKey X = new CssSizeStyleableFigureKey("x", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey Y = new CssSizeStyleableFigureKey("y", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey WIDTH = new CssSizeStyleableFigureKey("width", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey HEIGHT = new CssSizeStyleableFigureKey("height", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssRectangle2DStyleableMapAccessor BOUNDS = new CssRectangle2DStyleableMapAccessor("bounds", X, Y, WIDTH, HEIGHT);
    public final static CssSizeStyleableFigureKey SLICE_ORIGIN_X = new CssSizeStyleableFigureKey("sliceOriginX", DirtyMask.of(DirtyBits.NODE), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey SLICE_ORIGIN_Y = new CssSizeStyleableFigureKey("sliceOriginY", DirtyMask.of(DirtyBits.NODE), CssSize.ZERO);
    public final static CssPoint2DStyleableMapAccessor SLICE_ORIGIN = new CssPoint2DStyleableMapAccessor("sliceOrigin", SLICE_ORIGIN_X, SLICE_ORIGIN_Y);

    public SimpleSliceFigure() {
        this(0, 0, 1, 1);
    }

    public SimpleSliceFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
    }

    public SimpleSliceFigure(Rectangle2D rect) {
        this(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public void createHandles(HandleType handleType, @Nonnull List<Handle> list) {
        if (handleType == HandleType.POINT) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_POINT_OUTLINE));
            ResizeHandleKit.addCornerResizeHandles(this, list, Handle.STYLECLASS_HANDLE_POINT);
            list.add(new RelativePointHandle(this, Handle.STYLECLASS_HANDLE_CUSTOM, SLICE_ORIGIN));
        } else {
            super.createHandles(handleType, list); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Nonnull
    @Override
    public Bounds getBoundsInLocal() {
        return new BoundingBox(get(X).getConvertedValue(), get(Y).getConvertedValue(), get(WIDTH).getConvertedValue(), get(HEIGHT).getConvertedValue());
    }
    @Nonnull
    @Override
    public CssRectangle2D getCssBoundsInLocal() {
        return new CssRectangle2D(get(X), get(Y), get(WIDTH), get(HEIGHT));
    }

    @Override
    public Point2D getSliceOrigin() {
        Bounds b=getBoundsInLocal();
        Point2D p = getNonnull(SLICE_ORIGIN).getConvertedValue();
        return p.add(b.getMinX(),b.getMinY());
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        set(X, width.getValue()<0?x .add(width):x);
        set(Y, height.getValue()<0?y .add(height):y);
        set(WIDTH, width.abs());
        set(HEIGHT, height.abs());
    }

    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        Rectangle node = new Rectangle();
        node.setFill(new Color(0, 1.0, 0, 0.5));
        node.setStroke(Color.DARKRED);
        node.setStrokeType(StrokeType.INSIDE);
        return node;
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        Rectangle rectangleNode = (Rectangle) node;
        applyHideableFigureProperties(ctx, node);
        if (ctx.get(RenderContext.RENDERING_INTENT) != RenderingIntent.EDITOR) {
            rectangleNode.setVisible(false);
        }
        applyTransformableFigureProperties(ctx, rectangleNode);
        rectangleNode.setX(getNonnull(X).getConvertedValue());
        rectangleNode.setY(getNonnull(Y).getConvertedValue());
        rectangleNode.setWidth(getNonnull(WIDTH).getConvertedValue());
        rectangleNode.setHeight(getNonnull(HEIGHT).getConvertedValue());
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public boolean isSuitableParent(Figure newParent) {
        return Slice.super.isSuitableParent(newParent);
    }

}
