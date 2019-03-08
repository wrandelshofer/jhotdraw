/* @(#)TextAreaFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.PathConnector;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.geom.Shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

public class TextAreaFigure extends AbstractLeafFigure
        implements StrokableFigure, FillableFigure, TransformableFigure,
        ResizableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        ConnectableFigure, PathIterableFigure, RectangularFigure, ShapeableFigure,
        TextableFigure, TextFontableFigure, TextLayoutableFigure, TextFillableFigure, PaddableFigure {
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "TextArea";
    private Path path;

    @Override
    public Node createNode(RenderContext ctx) {
        Group g = new Group();
        g.setAutoSizeChildren(false);
        Path p = new Path();
        Text text = new Text();
        g.getChildren().addAll(p, text);
        return g;
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        Group g = (Group) node;
        Path p = (Path) g.getChildren().get(0);
        Text text = (Text) g.getChildren().get(1);
        applyShapeableProperties(ctx, p);
        applyStrokableFigureProperties(ctx, p);
        applyFillableFigureProperties(ctx, p);
        text.setText(getStyledNonnull(TEXT));
        applyTextFontableFigureProperties(ctx, text);
        applyTextLayoutableFigureProperties(ctx, text);
        applyTextFillableFigureProperties(ctx, text);
        applyTransformableFigureProperties(ctx, node);

        UnitConverter converter = ctx.getNonnull(RenderContext.UNIT_CONVERTER_KEY);
        Insets padding = getStyledNonnull(PADDING).getConvertedValue(converter);
        double size = text.getFont().getSize();
        Bounds bounds = getBoundsInLocal();

        double y;
        switch (text.getTextOrigin()) {
            case TOP:
            default:
                y = bounds.getMinY() + padding.getTop();
                break;
            case CENTER:
                y = bounds.getMinY() + bounds.getHeight() * 0.5;
                break;
            case BASELINE:
                y = bounds.getMinY() + size + padding.getTop();
                break;
            case BOTTOM:
                y = bounds.getMaxY() - padding.getBottom();
                break;
        }

        text.setX(bounds.getMinX() + padding.getLeft());
        text.setY(y);
        text.setWrappingWidth(bounds.getWidth() - padding.getLeft() - padding.getRight());

    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }


    @Override
    public void layout(@Nonnull RenderContext ctx) {
        if (path == null) {
            path = new Path();
        }
        applyShapeableProperties(ctx, path);
    }

    @Override
    public @Nullable Connector findConnector(Point2D pointInLocal, Figure connectingFigure) {
        return new PathConnector(new RelativeLocator(getBoundsInLocal(), pointInLocal));
    }

    @Override
    public PathIterator getPathIterator(@Nullable AffineTransform tx) {
        if (path == null) {
            layout(new SimpleRenderContext());
        }
        return path == null ? Shapes.emptyPathIterator() : Shapes.awtShapeFromFX(path).getPathIterator(tx);
    }
}
