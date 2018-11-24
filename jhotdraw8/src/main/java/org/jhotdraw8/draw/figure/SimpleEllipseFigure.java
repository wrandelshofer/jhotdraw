/* @(#)CircleFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

import static java.lang.Math.max;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Ellipse;

import javax.annotation.Nonnull;

import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.EllipseConnector;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Shapes;

/**
 * Renders a {@code javafx.scene.shape.Ellipse}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleEllipseFigure extends AbstractLeafFigure
        implements StrokableFigure, ResizableFigure, FillableFigure, TransformableFigure, HideableFigure, StyleableFigure,
        LockableFigure, CompositableFigure, ConnectableFigure, PathIterableFigure {

    public final static CssSizeStyleableFigureKey CENTER_X = new CssSizeStyleableFigureKey("centerX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey CENTER_Y = new CssSizeStyleableFigureKey("centerY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssPoint2DStyleableMapAccessor CENTER = new CssPoint2DStyleableMapAccessor("center", CENTER_X, CENTER_Y);
    public final static CssSizeStyleableFigureKey RADIUS_X = new CssSizeStyleableFigureKey("radiusX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ONE);
    public final static CssSizeStyleableFigureKey RADIUS_Y = new CssSizeStyleableFigureKey("radiusY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ONE);
    public final static CssPoint2DStyleableMapAccessor RADIUS = new CssPoint2DStyleableMapAccessor("radius", RADIUS_X, RADIUS_Y);
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Ellipse";

    public SimpleEllipseFigure() {
        this(0, 0, 2, 2);// the values must correspond to the default values of the property keys
    }

    public SimpleEllipseFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
    }

    public SimpleEllipseFigure(Rectangle2D rect) {
        reshapeInLocal(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Ellipse();
    }

    @Nonnull
    @Override
    public Connector findConnector(@Nonnull Point2D p, Figure prototype) {
        return new EllipseConnector(new RelativeLocator(getBoundsInLocal(), p));
    }

    @Nonnull
    @Override
    public Bounds getBoundsInLocal() {
        double rx = getNonnull(RADIUS_X).getConvertedValue();
        double ry = getNonnull(RADIUS_Y).getConvertedValue();
        double cx = getNonnull(CENTER_X).getConvertedValue();
        double cy = getNonnull(CENTER_Y).getConvertedValue();
        return new BoundingBox(cx - rx, cy - ry, rx * 2.0, ry * 2.0);
    }

    @Nonnull
    @Override
    public CssRectangle2D getCssBoundsInLocal() {
        CssSize rx = getNonnull(RADIUS_X);
        CssSize ry = getNonnull(RADIUS_Y);
        return new CssRectangle2D(getNonnull(CENTER_X).subtract(rx), getNonnull(CENTER_Y).subtract(ry), rx.multiply(2.0), ry.multiply(2.0));
    }


    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        Ellipse shape = new Ellipse();
        shape.setCenterX(getStyledNonnull(CENTER_X).getConvertedValue());
        shape.setCenterY(getStyledNonnull(CENTER_Y).getConvertedValue());

        double strokeWidth = getStyledNonnull(STROKE_WIDTH).getConvertedValue();
        double offset;
        switch (getStyledNonnull(STROKE_TYPE)) {
            case CENTERED:
            default:
                offset = 0;
                break;
            case INSIDE:
                offset = -strokeWidth * 0.5;
                break;
            case OUTSIDE:
                offset = strokeWidth * 0.5;
                break;
        }
        shape.setRadiusX(getStyledNonnull(RADIUS_X).getConvertedValue() + offset);
        shape.setRadiusY(getStyledNonnull(RADIUS_Y).getConvertedValue() + offset);
        return Shapes.awtShapeFromFX(shape).getPathIterator(tx);
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        CssSize rx = CssSize.max(width.multiply(0.5), CssSize.ZERO);
        CssSize ry = CssSize.max(height.multiply(0.5), CssSize.ZERO);
        set(CENTER_X, x.add(rx));
        set(CENTER_Y, y.add(ry));
        set(RADIUS_X, rx);
        set(RADIUS_Y, ry);
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        Ellipse n = (Ellipse) node;
        applyHideableFigureProperties(n);
        applyTransformableFigureProperties(ctx, n);
        applyStrokableFigureProperties(n);
        applyFillableFigureProperties(n);
        applyCompositableFigureProperties(n);
        applyStyleableFigureProperties(ctx, node);
        n.setCenterX(getStyledNonnull(CENTER_X).getConvertedValue());
        n.setCenterY(getStyledNonnull(CENTER_Y).getConvertedValue());
        n.setRadiusX(getStyledNonnull(RADIUS_X).getConvertedValue());
        n.setRadiusY(getStyledNonnull(RADIUS_Y).getConvertedValue());
        n.applyCss();
    }

}
