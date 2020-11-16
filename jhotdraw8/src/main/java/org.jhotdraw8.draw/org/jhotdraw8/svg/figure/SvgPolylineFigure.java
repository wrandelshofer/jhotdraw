/*
 * @(#)LineFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.figure.*;
import org.jhotdraw8.draw.key.DoubleListStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.FXGeom;
import org.jhotdraw8.geom.FXTransforms;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Represents an SVG 'polyline' element.
 *
 * @author Werner Randelshofer
 */
public class SvgPolylineFigure extends AbstractLeafFigure
        implements StyleableFigure, LockableFigure, SvgTransformableFigure, PathIterableFigure, HideableFigure, SvgPathLengthFigure, SvgDefaultableFigure,
        SvgElementFigure,SvgCompositableFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final @NonNull String TYPE_SELECTOR = "polyline";
    public static final @NonNull DoubleListStyleableKey POINTS = new DoubleListStyleableKey("points");

    @Override
    public Node createNode(RenderContext ctx) {
        Polyline n = new Polyline();
        n.setManaged(false);
        return n;
    }

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        Path2D.Double p = new Path2D.Double();
        ImmutableList<Double> points = get(POINTS);
        if (points != null) {
            for (int i = 0, n = points.size(); i < n - 1; i += 2) {
                if (i == 0) {
                    p.moveTo(points.get(0), points.get(1));
                } else {
                    p.lineTo(points.get(i), points.get(i + 1));
                }
            }
        }
        return p.getPathIterator(tx);
    }


    @Override
    public @NonNull Bounds getBoundsInLocal() {
        Path2D.Double p = new Path2D.Double();
        ImmutableList<Double> points = get(POINTS);
        if (points != null) {
            for (int i = 0, n = points.size(); i < n - 1; i += 2) {
                if (i == 0) {
                    p.moveTo(points.get(0), points.get(1));
                } else {
                    p.lineTo(points.get(i), points.get(i + 1));
                }
            }
        }
        return FXGeom.getBounds(p.getBounds2D());
    }

    @Override
    public @NonNull CssRectangle2D getCssLayoutBounds() {
        Bounds b = getBoundsInLocal();
        return new CssRectangle2D(b);
    }


    @Override
    public void reshapeInLocal(@NonNull Transform transform) {
        ImmutableList<Double> points = get(POINTS);
        if (points != null) {
            List<Double> t = new ArrayList<>(points.size());
            for (int i = 0, n = points.size(); i < n - 1; i += 2) {
                Point2D transformed = transform.transform(points.get(i), points.get(i + 1));
                t.add(transformed.getX());
                t.add(transformed.getY());
            }
            set(POINTS, ImmutableLists.ofCollection(t));
        }
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
        Polyline n = (Polyline) node;

        applyHideableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyTransformableFigureProperties(ctx, node);
        applySvgDefaultableFigureProperties(ctx, n);
        applySvgCompositableFigureProperties(ctx,n);

        ImmutableList<Double> points = get(POINTS);
        n.getPoints().setAll(points == null ? Collections.emptyList() : points.asList());

    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
