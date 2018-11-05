/* @(#)AbstractRegionFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.CssRectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
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
    @Nonnull
    public final static CssRectangle2DStyleableMapAccessor BOUNDS = SimpleRectangleFigure.BOUNDS;
    @Nonnull
    public final static CssSizeStyleableFigureKey HEIGHT = SimpleRectangleFigure.HEIGHT;
    @Nonnull
    public final static SvgPathStyleableFigureKey SHAPE = new SvgPathStyleableFigureKey("shape", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), null);
    @Nonnull
    public final static CssSizeStyleableFigureKey WIDTH = SimpleRectangleFigure.WIDTH;
    @Nonnull
    public final static CssSizeStyleableFigureKey X = SimpleRectangleFigure.X;
    @Nonnull
    public final static CssSizeStyleableFigureKey Y = SimpleRectangleFigure.Y;

    public final static BooleanStyleableFigureKey SHAPE_PRESERVE_RATIO_KEY = new BooleanStyleableFigureKey("ShapePreserveRatio", DirtyMask.of(DirtyBits.NODE), false);

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

    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Path();
    }

    @Nonnull
    @Override
    public CssRectangle2D getCssBoundsInLocal() {
        return getNonnull(BOUNDS);
    }

    @Nonnull
    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        if (pathElements == null) {
            pathElements = new Path2D.Float();
        }
        return pathElements.getPathIterator(tx);
    }

    @Override
    public void layout() {
        layoutPath();
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        set(X, width.getValue() < 0 ? x.add(width) : x);
        set(Y, height.getValue() < 0 ? y.add(height) : y);
        set(WIDTH, width.abs());
        set(HEIGHT, height.abs());
    }


    protected void updatePathNode(@Nonnull Path path) {
        path.getElements().setAll(Shapes.fxPathElementsFromAWT(pathElements.getPathIterator(null)));
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Path path = (Path) node;
        updatePathNode(path);
    }

    protected void layoutPath() {
        if (pathElements == null) {
            pathElements = new Path2D.Float();
        }
        pathElements.reset();

        String pathstr = getStyled(SHAPE);
        if (pathstr==null||pathstr.isEmpty()) {
            pathstr="0,0 1,0 1,1 0,1z";
        }

        double width = getStyledNonnull(WIDTH).getConvertedValue();
        double height = getStyledNonnull(HEIGHT).getConvertedValue();
        double x = getStyledNonnull(X).getConvertedValue();
        double y = getStyledNonnull(Y).getConvertedValue();
        final Bounds b;
        if (getStyledNonnull(SHAPE_PRESERVE_RATIO_KEY)) {
            AWTPathBuilder awtPathBuilder = new AWTPathBuilder(pathElements);
            try {
                Shapes.buildFromSvgString(awtPathBuilder, pathstr);
                java.awt.geom.Rectangle2D bounds2D = awtPathBuilder.build().getBounds2D();
                double pathRatio = bounds2D.getHeight()/bounds2D.getWidth();
                double regionRatio = height/width;
                if (pathRatio<regionRatio) {
                    b = new BoundingBox(
                            x,
                            y,
                            width,
                            pathRatio * width);
                }else{
                    b = new BoundingBox(
                            x,
                            y,
                            height/pathRatio,
                            height);
                }
                pathElements.reset();
            } catch (IOException e) {
                System.err.println("Illegal SVG path:: "+pathstr);
                return;
            }
        } else {
            b = new BoundingBox(
                    x,
                    y,
                    width,
                    height);
        }
        Shapes.reshape(pathstr, b, new AWTPathBuilder(pathElements));
    }
}
