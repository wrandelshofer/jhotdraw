/* @(#)AbstractRegionFigure.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.IOException;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.PathConnector;
import static org.jhotdraw8.draw.figure.StrokeableFigure.STROKE_TYPE;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.Rectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.SvgPathStyleableFigureKey;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;

/**
 * Renders a Shape (either a Rectangle or an SVGPath) inside a rectangular region.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public abstract class AbstractRegionFigure extends AbstractLeafFigure
        implements PathIterableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Region";

    public final static DoubleStyleableFigureKey X = RectangleFigure.X;
    public final static DoubleStyleableFigureKey Y = RectangleFigure.Y;
    public final static DoubleStyleableFigureKey WIDTH = RectangleFigure.WIDTH;
    public final static DoubleStyleableFigureKey HEIGHT = RectangleFigure.HEIGHT;
    public final static Rectangle2DStyleableMapAccessor BOUNDS = RectangleFigure.BOUNDS;
    public final static SvgPathStyleableFigureKey SHAPE = new SvgPathStyleableFigureKey("shape", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), null);

    protected transient List<PathElement> pathElements;

    public AbstractRegionFigure() {
        this(0, 0, 1, 1);
    }

    public AbstractRegionFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
        set(STROKE_TYPE, StrokeType.INSIDE);
    }

    public AbstractRegionFigure(Rectangle2D rect) {
        this(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public Bounds getBoundsInLocal() {
        return new BoundingBox(get(X), get(Y), get(WIDTH), get(HEIGHT));
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        updatePathElements();
        return Shapes.awtShapeFromFXPathElements(pathElements).getPathIterator(tx);
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        set(X, x + min(width, 0));
        set(Y, y + min(height, 0));
        set(WIDTH, abs(width));
        set(HEIGHT, abs(height));
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        return new Path();
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Path path = (Path) node;

        updatePathElements();

        path.getElements().setAll(pathElements);
    }

    protected void updatePathElements() {
        String pathstr = getStyled(SHAPE);

        if (pathElements == null) {
            pathElements = FXCollections.observableArrayList();
        }
        Bounds b = getBoundsInLocal();
        if (pathstr != null) {
            try {
                Shape shape = Shapes.awtShapeFromSvgString(pathstr);
                java.awt.geom.Rectangle2D r2d = shape.getBounds2D();
                Transform tx = Transforms.createReshapeTransform(
                        r2d.getX(), r2d.getY(), r2d.getWidth(), r2d.getHeight(),
                        b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight()
                );
                pathElements = Shapes.fxPathElementsFromAWT(shape.getPathIterator(Transforms.toAWT(tx)));
            } catch (IOException ex) {
                pathstr = null;
                Logger.getLogger(AbstractRegionFigure.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (pathstr == null) {
            pathElements.clear();
            pathElements.add(new MoveTo(b.getMinX(), b.getMinY()));
            pathElements.add(new LineTo(b.getMaxX(), b.getMinY()));
            pathElements.add(new LineTo(b.getMaxX(), b.getMaxY()));
            pathElements.add(new LineTo(b.getMinX(), b.getMaxY()));
            pathElements.add(new ClosePath());
        }
    }



    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
