/* @(#)AbstractLabelFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.CssInsetsStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.Rectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.SvgPathStyleableFigureKey;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.AWTPathBuilder;
import org.jhotdraw8.geom.FXPathBuilder;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.NineRegionsScalingBuilder;
import org.jhotdraw8.geom.Shapes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * A Label that can be placed anywhere on a drawing.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractLabelFigure extends AbstractLeafFigure
        implements TextFillableFigure, FillableFigure, StrokeableFigure,
        FontableFigure, ConnectableFigure, PathIterableFigure {

    public final static CssSizeStyleableFigureKey ORIGIN_X = new CssSizeStyleableFigureKey("originX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey ORIGIN_Y = new CssSizeStyleableFigureKey("originY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssPoint2DStyleableMapAccessor ORIGIN = new CssPoint2DStyleableMapAccessor("origin", ORIGIN_X, ORIGIN_Y);

    public final static CssSizeStyleableFigureKey PADDING_BOTTOM = new CssSizeStyleableFigureKey("paddingBottom", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey PADDING_LEFT = new CssSizeStyleableFigureKey("paddingLeft", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey PADDING_RIGHT = new CssSizeStyleableFigureKey("paddingRight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey PADDING_TOP = new CssSizeStyleableFigureKey("paddingTop", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssInsetsStyleableMapAccessor PADDING = new CssInsetsStyleableMapAccessor("padding", PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, PADDING_LEFT);
    public final static CssSizeStyleableFigureKey SHAPE_SLICE_BOTTOM = new CssSizeStyleableFigureKey("shapeSliceBottom", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey SHAPE_SLICE_LEFT = new CssSizeStyleableFigureKey("shapeSliceLeft", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey SHAPE_SLICE_RIGHT = new CssSizeStyleableFigureKey("shapeSliceRight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableFigureKey SHAPE_SLICE_TOP = new CssSizeStyleableFigureKey("shapeSliceTop", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    /**
     * This property specifies inward offsets from the top, right, bottom, and
     * left edges of the border image defined by the {@link #SHAPE_BOUNDS}
     * property, dividing it into nine regions. Percentages are relative to the
     * shape bounds: the width for the horizontal offsets, the height for the
     * vertical offsets. Numbers represent pixel units in the image.
     * <p>
     * See
     * <a href="https://www.w3.org/TR/css3-background/#border-image-slice">CSS3
     * Background: border-image-slice</a>.
     */
    public final static CssInsetsStyleableMapAccessor SHAPE_SLICE = new CssInsetsStyleableMapAccessor("shapeSlice", SHAPE_SLICE_TOP, SHAPE_SLICE_RIGHT, SHAPE_SLICE_BOTTOM, SHAPE_SLICE_LEFT);
    /**
     * This property specifies the bounds of a {@link#SHAPE} property. If the
     * bounds are null or empty, then the bounds of the shape are used.
     */
    public final static DoubleStyleableFigureKey SHAPE_BOUNDS_X = new DoubleStyleableFigureKey("shapeBoundsX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey SHAPE_BOUNDS_Y = new DoubleStyleableFigureKey("shapeBoundsY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey SHAPE_BOUNDS_WIDTH = new DoubleStyleableFigureKey("shapeBoundsWidth", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey SHAPE_BOUNDS_HEIGHT = new DoubleStyleableFigureKey("shapeBoundsHeight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static Rectangle2DStyleableMapAccessor SHAPE_BOUNDS = new Rectangle2DStyleableMapAccessor("shapeBounds", SHAPE_BOUNDS_X, SHAPE_BOUNDS_Y, SHAPE_BOUNDS_WIDTH, SHAPE_BOUNDS_HEIGHT);
    /**
     * Defines the border image as an SVG path.
     */
    @Nonnull
    public final static SvgPathStyleableFigureKey SHAPE = new SvgPathStyleableFigureKey("shape", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), null);
    private static final String SVG_SQUARE = "M 0,0 1,0 1,1 0,1 Z";

    @Nonnull
    public final static Key<Bounds> BOUNDS_IN_LOCAL_CACHE_KEY = new ObjectKey<>("boundsInLocal", Bounds.class, null, true, true, null);

    public AbstractLabelFigure() {
        this(0, 0);
    }

    public AbstractLabelFigure(Point2D position) {
        this(position.getX(), position.getY());
    }

    public AbstractLabelFigure(double x, double y) {
        set(ORIGIN, new CssPoint2D(x, y));
        set(FILL, null);
        set(STROKE, null);
    }
    
    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        Group g = new Group();
        g.setAutoSizeChildren(false);
        Path p = new Path();
        Text text = new Text();
        g.getChildren().addAll(p, text);
        return g;
    }

    @Override
    public Connector findConnector(@Nonnull Point2D p, Figure prototype) {
        return new RectangleConnector(new RelativeLocator(getBoundsInLocal(), p));
    }

    @Nonnull
    @Override
    public Bounds getBoundsInLocal() {
        Bounds boundsInLocal = getCachedValue(BOUNDS_IN_LOCAL_CACHE_KEY);
        return boundsInLocal == null ? getLayoutBounds() : boundsInLocal;
    }
    @Nonnull
    @Override
    public CssRectangle2D getCssBoundsInLocal() {
        return new CssRectangle2D(getBoundsInLocal());
    }

    /**
     * Returns the bounds of the node for layout calculations. These bounds
     * include the text of the node and the padding.
     *
     * @return the layout bounds
     */
    @Nonnull
    public Bounds getLayoutBounds() {
        Text  textNode = new Text();
        updateTextNode(null, textNode);
        Bounds b = textNode.getLayoutBounds();
        Insets i = getStyledNonnull(PADDING).getConvertedValue();

        return new BoundingBox(
                b.getMinX() - i.getLeft(),
                b.getMinY() - i.getTop(),
                b.getWidth() + i.getLeft() + i.getRight(),
                textNode.getBaselineOffset() + i.getTop() + i.getBottom());
    }
    /**
     * Returns the bounds of the text node for layout calculations. These bounds
     * only includes the text - without padding.
     *
     * @return the layout bounds of the text
     */
    @Nonnull
    protected Bounds getTextBounds() {
        Text  textNode = new Text();
        updateTextNode(null, textNode);
        Bounds b = textNode.getLayoutBounds();
        return b;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        Text tn = new Text();
        tn.setText(getText(null));
        tn.setX(getStyledNonnull(ORIGIN).getX().getConvertedValue());
        tn.setY(getStyledNonnull(ORIGIN).getY().getConvertedValue());
        tn.setBoundsType(TextBoundsType.VISUAL);
        applyFontableFigureProperties(null, tn);
        return Shapes.awtShapeFromFX(tn).getPathIterator(tx);
    }

    @Nullable
    protected abstract String getText(RenderContext ctx);


    @Override
    public void layout() {
        Bounds b = getLayoutBounds();
        setCachedValue(BOUNDS_IN_LOCAL_CACHE_KEY, b);
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        Bounds lb = getLayoutBounds();
        Insets i = getStyledNonnull(PADDING).getConvertedValue();
        set(ORIGIN, new CssPoint2D(x.getConvertedValue() + i.getLeft(), y.getConvertedValue() + lb.getHeight() - i.getBottom()));
    }

    @Override
    public void translateInLocal(@Nonnull CssPoint2D delta) {
        set(ORIGIN, getNonnull(ORIGIN).add(delta));
    }

    protected void updateGroupNode(RenderContext ctx, Group node) {

    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Group g = (Group) node;
        Path p = (Path) g.getChildren().get(0);
        Text t = (Text) g.getChildren().get(1);
        updateGroupNode(ctx, g);
        updatePathNode(ctx, p);
        updateTextNode(ctx, t);
    }

    protected void updatePathNode(RenderContext ctx, @Nonnull Path node) {
        applyFillableFigureProperties(node);
        applyStrokableFigureProperties(node);

        String content = getStyled(SHAPE);
        if (content == null || content.trim().isEmpty()) {
            content = SVG_SQUARE;
        }
        Bounds b = getBoundsInLocal();

        try {
            AWTPathBuilder builder = new AWTPathBuilder(new Path2D.Float());
            Shapes.buildFromSvgString(builder, content);
            Path2D path = builder.build();

            FXPathBuilder builder2 = new FXPathBuilder();

            javafx.geometry.Rectangle2D shapeBounds = getStyled(SHAPE_BOUNDS);

            final Bounds srcBounds = shapeBounds == null || Geom.isEmpty(shapeBounds) ? Geom.getBounds(path) : Geom.getBounds(shapeBounds);
            Insets shapeSlice = getStyled(SHAPE_SLICE).getConvertedValue(srcBounds.getWidth(), srcBounds.getHeight());
            final NineRegionsScalingBuilder nineRegionsScalingBuilder = new NineRegionsScalingBuilder(builder2, srcBounds, shapeSlice, b);

            Shapes.buildFromPathIterator(nineRegionsScalingBuilder, path.getPathIterator(null));
            List<PathElement> elements = builder2.getElements();
            node.getElements().setAll(elements);
            node.setVisible(true);
        } catch (IOException ex) {
            node.setVisible(false);
            return;
        }
    }

    protected void updateTextNode(RenderContext ctx, @Nonnull Text tn) {
        final String text = getText(ctx);
        if (!Objects.equals(text, tn.getText())) {
            tn.setText(text);
        }
        tn.setX(getStyledNonnull(ORIGIN_X).getConvertedValue());
        tn.setY(getStyledNonnull(ORIGIN_Y).getConvertedValue());
        applyTextFillableFigureProperties(ctx, tn);
        applyFontableFigureProperties(ctx, tn);
    }

}
