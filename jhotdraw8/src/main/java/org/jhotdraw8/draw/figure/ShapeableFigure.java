/* @(#)ShapeableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssInsetsStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.NullableSvgPathStyleableFigureKey;
import org.jhotdraw8.draw.key.Rectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.AWTPathBuilder;
import org.jhotdraw8.geom.FXPathBuilder;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.NineRegionsScalingBuilder;
import org.jhotdraw8.geom.Shapes;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.List;

public interface ShapeableFigure extends Figure {
    CssSizeStyleableFigureKey SHAPE_SLICE_BOTTOM = new CssSizeStyleableFigureKey("shapeSliceBottom", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    CssSizeStyleableFigureKey SHAPE_SLICE_LEFT = new CssSizeStyleableFigureKey("shapeSliceLeft", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    CssSizeStyleableFigureKey SHAPE_SLICE_RIGHT = new CssSizeStyleableFigureKey("shapeSliceRight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    CssSizeStyleableFigureKey SHAPE_SLICE_TOP = new CssSizeStyleableFigureKey("shapeSliceTop", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
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
    CssInsetsStyleableMapAccessor SHAPE_SLICE = new CssInsetsStyleableMapAccessor("shapeSlice", SHAPE_SLICE_TOP, SHAPE_SLICE_RIGHT, SHAPE_SLICE_BOTTOM, SHAPE_SLICE_LEFT);
    /**
     * This property specifies the bounds of a {@link #SHAPE} property. If the
     * bounds are null or empty, then the bounds of the shape are used.
     */
    DoubleStyleableFigureKey SHAPE_BOUNDS_X = new DoubleStyleableFigureKey("shapeBoundsX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    DoubleStyleableFigureKey SHAPE_BOUNDS_Y = new DoubleStyleableFigureKey("shapeBoundsY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    DoubleStyleableFigureKey SHAPE_BOUNDS_WIDTH = new DoubleStyleableFigureKey("shapeBoundsWidth", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    DoubleStyleableFigureKey SHAPE_BOUNDS_HEIGHT = new DoubleStyleableFigureKey("shapeBoundsHeight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    Rectangle2DStyleableMapAccessor SHAPE_BOUNDS = new Rectangle2DStyleableMapAccessor("shapeBounds", SHAPE_BOUNDS_X, SHAPE_BOUNDS_Y, SHAPE_BOUNDS_WIDTH, SHAPE_BOUNDS_HEIGHT);
    /**
     * Defines the border image as an SVG path.
     */
    @Nonnull NullableSvgPathStyleableFigureKey SHAPE = new NullableSvgPathStyleableFigureKey("shape", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), null);
    String SVG_SQUARE = "M 0,0 1,0 1,1 0,1 Z";

    default void applyShapeableProperties(RenderContext ctx, @Nonnull Path node) {
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
            Insets shapeSlice = getStyledNonnull(SHAPE_SLICE).getConvertedValue(srcBounds.getWidth(), srcBounds.getHeight());
            final NineRegionsScalingBuilder nineRegionsScalingBuilder = new NineRegionsScalingBuilder(builder2, srcBounds, shapeSlice, b);

            Shapes.buildFromPathIterator(nineRegionsScalingBuilder, path.getPathIterator(null));
            List<PathElement> elements = builder2.getElements();
            node.getElements().setAll(elements);
            node.setVisible(true);
        } catch (IOException ex) {
            node.setVisible(false);
        }
    }
}
