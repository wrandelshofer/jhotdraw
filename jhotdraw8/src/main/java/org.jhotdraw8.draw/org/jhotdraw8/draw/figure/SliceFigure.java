/*
 * @(#)SliceFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.handle.BoundsInLocalOutlineHandle;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.handle.RelativePointHandle;
import org.jhotdraw8.draw.handle.ResizeHandleKit;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssRectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;

import java.util.List;

/**
 * This is a special figure which is used to segment a drawing into tiles, when
 * exporting it using the {@link BitmapExportOutputFormat}.
 * <p>
 * This figure renders only with rendering intent
 * {@link RenderingIntent#EDITOR}.
 *
 * @author Werner Randelshofer
 */
public class SliceFigure extends AbstractLeafFigure implements Slice, TransformableFigure, ResizableFigure, HideableFigure, LockableFigure, StyleableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Slice";

    @Nullable
    public final static CssSizeStyleableKey X = new CssSizeStyleableKey("x", CssSize.ZERO);
    @Nullable
    public final static CssSizeStyleableKey Y = new CssSizeStyleableKey("y", CssSize.ZERO);
    @Nullable
    public final static CssSizeStyleableKey WIDTH = new CssSizeStyleableKey("width", CssSize.ZERO);
    @Nullable
    public final static CssSizeStyleableKey HEIGHT = new CssSizeStyleableKey("height", CssSize.ZERO);
    @Nullable
    public final static CssRectangle2DStyleableMapAccessor BOUNDS = new CssRectangle2DStyleableMapAccessor("bounds", X, Y, WIDTH, HEIGHT);
    @Nullable
    public final static CssSizeStyleableKey SLICE_ORIGIN_X = new CssSizeStyleableKey("sliceOriginX", CssSize.ZERO);
    @Nullable
    public final static CssSizeStyleableKey SLICE_ORIGIN_Y = new CssSizeStyleableKey("sliceOriginY", CssSize.ZERO);
    @Nullable
    public final static CssPoint2DStyleableMapAccessor SLICE_ORIGIN = new CssPoint2DStyleableMapAccessor("sliceOrigin", SLICE_ORIGIN_X, SLICE_ORIGIN_Y);

    public SliceFigure() {
        this(0, 0, 1, 1);
    }

    public SliceFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
    }

    public SliceFigure(@NonNull Rectangle2D rect) {
        this(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public void createHandles(HandleType handleType, @NonNull List<Handle> list) {
        if (handleType == HandleType.POINT) {
            list.add(new BoundsInLocalOutlineHandle(this));
            ResizeHandleKit.addCornerResizeHandles(this, list);
            list.add(new RelativePointHandle(this, SLICE_ORIGIN));
        } else {
            super.createHandles(handleType, list); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @NonNull
    @Override
    public Bounds getLayoutBounds() {
        return new BoundingBox(get(X).getConvertedValue(), get(Y).getConvertedValue(), get(WIDTH).getConvertedValue(), get(HEIGHT).getConvertedValue());
    }

    @NonNull
    @Override
    public CssRectangle2D getCssLayoutBounds() {
        return new CssRectangle2D(get(X), get(Y), get(WIDTH), get(HEIGHT));
    }

    @Override
    public Point2D getSliceOrigin() {
        Bounds b = getLayoutBounds();
        Point2D p = getNonNull(SLICE_ORIGIN).getConvertedValue();
        return p.add(b.getMinX(), b.getMinY());
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        set(X, width.getValue() < 0 ? x.add(width) : x);
        set(Y, height.getValue() < 0 ? y.add(height) : y);
        set(WIDTH, width.abs());
        set(HEIGHT, height.abs());
    }

    @NonNull
    @Override
    public Node createNode(RenderContext drawingView) {
        Rectangle node = new Rectangle();
        node.setFill(new Color(0, 1.0, 0, 0.5));
        node.setStroke(Color.DARKRED);
        node.setStrokeType(StrokeType.INSIDE);
        return node;
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Rectangle rectangleNode = (Rectangle) node;
        applyHideableFigureProperties(ctx, node);
        if (ctx.get(RenderContext.RENDERING_INTENT) != RenderingIntent.EDITOR) {
            rectangleNode.setVisible(false);
        }
        applyTransformableFigureProperties(ctx, rectangleNode);
        rectangleNode.setX(getNonNull(X).getConvertedValue());
        rectangleNode.setY(getNonNull(Y).getConvertedValue());
        rectangleNode.setWidth(getNonNull(WIDTH).getConvertedValue());
        rectangleNode.setHeight(getNonNull(HEIGHT).getConvertedValue());
    }

    @NonNull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public boolean isSuitableParent(@NonNull Figure newParent) {
        return Slice.super.isSuitableParent(newParent);
    }

    @Override
    public @NonNull Bounds getBoundsInLocal() {
        return getLayoutBounds();
    }
}
