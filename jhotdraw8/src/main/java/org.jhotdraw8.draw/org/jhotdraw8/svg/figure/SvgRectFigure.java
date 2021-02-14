/*
 * @(#)SvgRectFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
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
import org.jhotdraw8.draw.key.NullableCssSizeStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.FXTransforms;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

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
    public static final @NonNull NullableCssSizeStyleableKey RX = new NullableCssSizeStyleableKey("rx", null);
    public static final @NonNull NullableCssSizeStyleableKey RY = new NullableCssSizeStyleableKey("ry", null);
    public static final @NonNull CssSizeStyleableKey WIDTH = new CssSizeStyleableKey("width", CssSize.ZERO);
    public static final @NonNull CssSizeStyleableKey HEIGHT = new CssSizeStyleableKey("height", CssSize.ZERO);

    @Override
    public Node createNode(RenderContext ctx) {
        Group g=new Group();
        // We cannot use a Rectangle here, because JavaFX does not draw
        // a Rectangle with the same algorithm that SVG uses.
        Path n0 = new Path();
        Path n1 = new Path();
        n0.setManaged(false);
        n1.setManaged(false);
        g.getChildren().addAll(n0,n1);
        return g;
    }

    private Point2D getRxRy(RenderContext ctx) {
        CssSize rxNullable = get(RX);
        CssSize ryNullable = get(RY);
        final double rx,ry;
        UnitConverter unit = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        if (rxNullable==null&&ryNullable==null) {
            rx=ry=0;
        } else if (rxNullable==null){
            rx=ry=ryNullable.getConvertedValue(unit);
        }else if (ryNullable==null) {
            rx=ry=rxNullable.getConvertedValue(unit);
        }else{
            rx=rxNullable.getConvertedValue(unit);
            ry=ryNullable.getConvertedValue(unit);
        }
        return new Point2D(rx,ry);
    }

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {

            UnitConverter unit = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        Point2D rxRy = getRxRy(ctx);
        RoundRectangle2D.Double p = new RoundRectangle2D.Double(
                getNonNull(X).getConvertedValue(unit),
                getNonNull(Y).getConvertedValue(unit),
                getNonNull(WIDTH).getConvertedValue(unit),
                getNonNull(HEIGHT).getConvertedValue(unit),
                rxRy.getX()*2,
                rxRy.getY()*2
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
        Path n0 = (Path) g.getChildren().get(0);
        Path n1 = (Path) g.getChildren().get(1);

        applyHideableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyTransformableFigureProperties(ctx, node);
        applySvgDefaultableCompositingProperties(ctx,node);
        applySvgShapeProperties(ctx,n0,n1);

        double x = getNonNull(X).getConvertedValue(unit);
        double y = getNonNull(Y).getConvertedValue(unit);
        Point2D rxRy = getRxRy(ctx);
        double rx=min(rxRy.getX(),width*0.5);
        double ry=min(rxRy.getY(),height*0.5);

        // Algorithm from https://www.w3.org/TR/SVGTiny12/shapes.html#RectElement
        List<PathElement> l=new ArrayList<>();
        l.add(new MoveTo(x+rx,y));
        l.add(new LineTo(x+width-rx,y));
        if (rx>0||ry>0) {
            l.add(new MoveTo(x+rx,y));
            l.add(new LineTo(x+width-rx,y));
            l.add(new ArcTo(rx,ry,0,x+width,y+ry,false,true));
            l.add(new LineTo(x+width,y+height-ry));
            l.add(new ArcTo(rx,ry,0,x+width-rx,y+height,false,true));
            l.add(new LineTo(x+rx,y+height));
            l.add(new ArcTo(rx,ry,0,x,y+height-ry,false,true));
            l.add(new LineTo(x,y+ry));
            l.add(new ArcTo(rx,ry,0,x+rx,y,false,true));
            l.add(new ClosePath());
        }else{
            l.add(new MoveTo(x,y));
            l.add(new LineTo(x+width,y));
            l.add(new LineTo(x+width,y+height));
            l.add(new LineTo(x,y+height));
            l.add(new ClosePath());
        }
        n0.getElements().setAll(l);
        n1.getElements().setAll(l);

        n0.applyCss();
        n1.applyCss();

    }

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
