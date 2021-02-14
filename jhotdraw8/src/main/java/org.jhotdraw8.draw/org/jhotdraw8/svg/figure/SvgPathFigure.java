/*
 * @(#)SvgPathFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.figure.AbstractLeafFigure;
import org.jhotdraw8.draw.figure.HideableFigure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.key.StringStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.AWTPathBuilder;
import org.jhotdraw8.geom.FXPathBuilder;
import org.jhotdraw8.geom.FXTransformPathBuilder;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.xml.text.XmlNumberConverter;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.text.ParseException;


/**
 * Represents an SVG 'path' element.
 *
 * @author Werner Randelshofer
 */
public class SvgPathFigure extends AbstractLeafFigure
        implements StyleableFigure, LockableFigure, SvgTransformableFigure, PathIterableFigure, HideableFigure, SvgPathLengthFigure, SvgDefaultableFigure,
        SvgElementFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final @NonNull String TYPE_SELECTOR = "path";
    public static final @NonNull StringStyleableKey D = new StringStyleableKey("d");

    @Override
    public Node createNode(RenderContext ctx) {
        Group g=new Group();
        javafx.scene.shape.Path n0 = new javafx.scene.shape.Path();
        javafx.scene.shape.Path n1 = new javafx.scene.shape.Path();
        n0.setManaged(false);
        n1.setManaged(false);
        g.getChildren().addAll(n0,n1);
        return g;
    }

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        AWTPathBuilder b = new AWTPathBuilder();
        String d = get(D);
        if (d != null) {
            try {
                Shapes.buildFromSvgString(b, d);
            } catch (ParseException e) {
                // bail
            }
        }
        return b.build().getPathIterator(tx);
    }


    @Override
    public @NonNull Bounds getBoundsInLocal() {
        AWTPathBuilder b = new AWTPathBuilder();
        String d = get(D);
        if (d != null) {
            try {
                Shapes.buildFromSvgString(b, d);
            } catch (ParseException e) {
                // bail
            }
        }
        Rectangle bounds = b.build().getBounds();
        return new BoundingBox(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }

    @Override
    public @NonNull CssRectangle2D getCssLayoutBounds() {
        Bounds b = getBoundsInLocal();
        return new CssRectangle2D(b);
    }


    @Override
    public void reshapeInLocal(@NonNull Transform transform) {
        FXPathBuilder bb = new FXPathBuilder();
        FXTransformPathBuilder b = new FXTransformPathBuilder(bb);
        b.setTransform(transform);
        String d = get(D);
        if (d != null) {
            try {
                Shapes.buildFromSvgString(b, d);
            } catch (ParseException e) {
                // bail
            }
        }

        set(D, Shapes.svgStringFromElements(bb.getElements(), new XmlNumberConverter()));
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
        javafx.scene.shape.Path n0 = (javafx.scene.shape.Path) g.getChildren().get(0);
        javafx.scene.shape.Path n1 = (javafx.scene.shape.Path) g.getChildren().get(1);

        applyHideableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyTransformableFigureProperties(ctx, node);
        applySvgDefaultableCompositingProperties(ctx,node);
        applySvgShapeProperties(ctx,n0,n1);

        FXPathBuilder bb = new FXPathBuilder();
        String d = get(D);
        if (d != null) {
            try {
                Shapes.buildFromSvgString(bb, d);
            } catch (ParseException e) {
                // bail
            }
        }
        n0.getElements().setAll(bb.getElements());
        n1.getElements().setAll(bb.getElements());
    }

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
