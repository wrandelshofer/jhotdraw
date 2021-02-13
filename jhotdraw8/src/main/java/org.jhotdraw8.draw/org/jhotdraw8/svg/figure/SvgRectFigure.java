/*
 * @(#)SvgRectFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.figure.AbstractLeafFigure;
import org.jhotdraw8.draw.figure.HideableFigure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.FXTransforms;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.RoundRectangle2D;

/**
 * Represents an SVG 'rect' element.
 *
 * @author Werner Randelshofer
 */
public class SvgRectFigure extends AbstractLeafFigure
        implements StyleableFigure, LockableFigure, SvgTransformableFigure,
        PathIterableFigure, HideableFigure, SvgPathLengthFigure,
        SvgDefaultableFigure,  SvgElementFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final @NonNull String TYPE_SELECTOR = "rect";
    public static final @NonNull CssSizeStyleableKey X = new CssSizeStyleableKey("x", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey Y = new CssSizeStyleableKey("y", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey RX = new CssSizeStyleableKey("rx", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey RY = new CssSizeStyleableKey("ry", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey WIDTH = new CssSizeStyleableKey("width", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey HEIGHT = new CssSizeStyleableKey("height", CssSize.ZERO);

    @Override
    public Node createNode(RenderContext ctx) {
        Group g=new Group();
        Rectangle n0 = new Rectangle();
        Rectangle n1 = new Rectangle();
        n0.setManaged(false);
        n1.setManaged(false);
        g.getChildren().addAll(n0,n1);
        return g;
    }

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        RoundRectangle2D.Double p = new RoundRectangle2D.Double(
                getNonNull(X).getConvertedValue(),
                getNonNull(Y).getConvertedValue(),
                getNonNull(WIDTH).getConvertedValue(),
                getNonNull(HEIGHT).getConvertedValue(),
                getNonNull(RX).getConvertedValue(),
                getNonNull(RY).getConvertedValue()
        );
        return p.getPathIterator(tx);
    }


    @Override
    public @NonNull Bounds getBoundsInLocal() {
        return getCssLayoutBounds().getConvertedBoundsValue();
    }

    @Override
    public @NonNull CssRectangle2D getCssLayoutBounds() {
        CssSize x = getNonNull(X);
        CssSize y = getNonNull(Y);
        CssSize w = getNonNull(WIDTH);
        CssSize h = getNonNull(HEIGHT);
        return new CssRectangle2D(x, y, w, h);
    }


    @Override
    public void reshapeInLocal(@NonNull Transform transform) {
        CssSize x = getNonNull(X);
        CssSize y = getNonNull(Y);
        CssSize w = getNonNull(WIDTH);
        CssSize h = getNonNull(HEIGHT);

        CssPoint2D txy = new CssPoint2D(transform.transform(x.getConvertedValue(), y.getConvertedValue()));
        CssPoint2D twh = new CssPoint2D(transform.deltaTransform(w.getConvertedValue(), h.getConvertedValue()));
        set(X, txy.getX());
        set(Y, txy.getY());
        set(WIDTH, twh.getX());
        set(HEIGHT, twh.getY());

    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        reshapeInLocal(x.getConvertedValue(), y.getConvertedValue(), width.getConvertedValue(), height.getConvertedValue());
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        reshapeInLocal(FXTransforms.createReshapeTransform(getLayoutBounds(), x, y, width, height));
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Group g=(Group)node;
        UnitConverter unit = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        double width = getNonNull(WIDTH).getConvertedValue(unit);
        double height = getNonNull(HEIGHT).getConvertedValue(unit);
        if (width<=0||height<=0) {
            g.setVisible(false);
            return;
        }
        Rectangle n0 = (Rectangle) g.getChildren().get(0);
        Rectangle n1 = (Rectangle) g.getChildren().get(1);

        applyHideableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyTransformableFigureProperties(ctx, node);
        applySvgDefaultableCompositingProperties(ctx,node);
        applySvgShapeProperties(ctx,n0,n1);

        double x = getNonNull(X).getConvertedValue(unit);
        double y = getNonNull(Y).getConvertedValue(unit);
        double aw = getNonNull(RX).getConvertedValue(unit);
        double ah = getNonNull(RY).getConvertedValue(unit);
        n0.setX(x);
        n0.setY(y);
        n0.setArcWidth(aw);
        n0.setArcHeight(ah);
        n0.setWidth(width);
        n0.setHeight(height);
        n0.applyCss();
        n1.setX(x);
        n1.setY(y);
        n1.setArcWidth(aw);
        n1.setArcHeight(ah);
        n1.setWidth(width);
        n1.setHeight(height);
        n1.applyCss();

    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
