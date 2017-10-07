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
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.EllipseConnector;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.Point2DStyleableMapAccessor;
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
        implements StrokeableFigure, ResizableFigure, FillableFigure, TransformableFigure, HideableFigure, StyleableFigure, 
        LockableFigure, CompositableFigure,ConnectableFigure,PathIterableFigure {

    public final static DoubleStyleableFigureKey CENTER_X = new DoubleStyleableFigureKey("centerX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey CENTER_Y = new DoubleStyleableFigureKey("centerY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static Point2DStyleableMapAccessor CENTER = new Point2DStyleableMapAccessor("center", CENTER_X, CENTER_Y);
    public final static DoubleStyleableFigureKey RADIUS_X = new DoubleStyleableFigureKey("radiusX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    public final static DoubleStyleableFigureKey RADIUS_Y = new DoubleStyleableFigureKey("radiusY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    public final static Point2DStyleableMapAccessor RADIUS = new Point2DStyleableMapAccessor("radius", RADIUS_X, RADIUS_Y);
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

    @Override
    public Node createNode(RenderContext drawingView) {
        return new Ellipse();
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return new EllipseConnector(new RelativeLocator(getBoundsInLocal(),p));
    }

    @Override
    public Bounds getBoundsInLocal() {
        double rx = get(RADIUS_X);
        double ry = get(RADIUS_Y);
        return new BoundingBox(get(CENTER_X) - rx, get(CENTER_Y) - ry, rx * 2.0, ry * 2.0);
    }


    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        Ellipse shape=new Ellipse();
       shape.setCenterX(get(CENTER_X));
        shape.setCenterY(get(CENTER_Y));
        shape.setRadiusX(get(RADIUS_X));
        shape.setRadiusY(get(RADIUS_Y));
       return Shapes.awtShapeFromFX(shape).getPathIterator(tx);
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        double rx = max(0.0, width) / 2.0;
        double ry = max(0.0, height) / 2.0;
        set(CENTER_X, x + rx);
        set(CENTER_Y, y + ry);
        set(RADIUS_X, rx);
        set(RADIUS_Y, ry);
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Ellipse n = (Ellipse) node;
        applyHideableFigureProperties(n);
        applyTransformableFigureProperties(n);
        applyStrokeableFigureProperties(n);
        applyFillableFigureProperties(n);
        applyCompositableFigureProperties(n);
        applyStyleableFigureProperties(ctx, node);
        n.setCenterX(getStyled(CENTER_X));
        n.setCenterY(getStyled(CENTER_Y));
        n.setRadiusX(getStyled(RADIUS_X));
        n.setRadiusY(getStyled(RADIUS_Y));
        n.applyCss();
    }

}
