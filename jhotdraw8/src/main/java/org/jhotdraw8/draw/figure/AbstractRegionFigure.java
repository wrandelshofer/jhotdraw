/* @(#)AbstractRegionFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import javafx.css.StyleOrigin;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeType;
import static org.jhotdraw8.draw.figure.StrokeableFigure.STROKE_TYPE;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.Rectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.SvgPathStyleableFigureKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.AWTPathBuilder;
import org.jhotdraw8.geom.Shapes;

/**
 * Renders a Shape (either a Rectangle or an SVGPath) inside a rectangular region.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractRegionFigure extends AbstractLeafFigure
        implements PathIterableFigure {
    public final static Rectangle2DStyleableMapAccessor BOUNDS = SimpleRectangleFigure.BOUNDS;
    public final static DoubleStyleableFigureKey HEIGHT = SimpleRectangleFigure.HEIGHT;
    public final static SvgPathStyleableFigureKey SHAPE = new SvgPathStyleableFigureKey("shape", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), null);
    public final static DoubleStyleableFigureKey WIDTH = SimpleRectangleFigure.WIDTH;
    public final static DoubleStyleableFigureKey X = SimpleRectangleFigure.X;
    public final static DoubleStyleableFigureKey Y = SimpleRectangleFigure.Y;

    private transient Path2D.Float pathElements;

    public AbstractRegionFigure() {
        this(0, 0, 1, 1);
    }

    public AbstractRegionFigure(double x, double y, double width, double height) {
        reshapeInLocal(x, y, width, height);
        setStyled(StyleOrigin.USER_AGENT, STROKE_TYPE, StrokeType.INSIDE);
    }

    public AbstractRegionFigure(Rectangle2D rect) {
        this(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Path();
    }

    @Override
    public Bounds getBoundsInLocal() {
        return new BoundingBox(get(X), get(Y), get(WIDTH), get(HEIGHT));
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        if (pathElements==null) pathElements=new Path2D.Float();
        return pathElements.getPathIterator(tx);
    }

    @Override
    public void layout() {
        layoutPath();
    }
    
    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        set(X, x + min(width, 0));
        set(Y, y + min(height, 0));
        set(WIDTH, abs(width));
        set(HEIGHT, abs(height));
    }



    protected void updatePathNode(Path path) {
        path.getElements().setAll(Shapes.fxPathElementsFromAWT(pathElements.getPathIterator(null)));
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Path path = (Path) node;
        updatePathNode(path);
    }

    protected void layoutPath() {
        String pathstr = getStyled(SHAPE);

        if (pathElements == null) {
            pathElements = pathElements=new Path2D.Float();
        }
        pathElements.reset();
        Bounds b = getBoundsInLocal();
        Shapes.reshape(pathstr, b, new AWTPathBuilder(pathElements));
    }
}
