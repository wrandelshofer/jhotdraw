/*
 * @(#)TextFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.CssPoint2DStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.geom.Shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * {@code TextFigure} is a {@code TextFontableFigure} which supports stroking and
 * filling of the text.
 *
 * @author Werner Randelshofer
 */
public class TextFigure extends AbstractLeafFigure
        implements StrokableFigure, FillableFigure, TransformableFigure, TextFontableFigure, TextLayoutableFigure,
        TextableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        ConnectableFigure, PathIterableFigure, TextEditableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Text";
    public final static CssPoint2DStyleableKey ORIGIN = new CssPoint2DStyleableKey("origin", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT), new CssPoint2D(0, 0));

    private Text textNode;

    public TextFigure() {
        this(0, 0, "");
    }

    public TextFigure(Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }

    public TextFigure(double x, double y, String text) {
        set(TEXT, text);
        set(ORIGIN, new CssPoint2D(x, y));
    }

    @Nonnull
    @Override
    public Bounds getBoundsInLocal() {
        // FIXME the text node should be computed during layout
        if (textNode == null) {
            layout(new SimpleRenderContext());
        }

        Bounds b = textNode.getBoundsInLocal();
        return new BoundingBox(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public TextEditorData getTextEditorDataFor(Point2D pointInLocal, Node node) {
        return new TextEditorData(this, getBoundsInLocal(), TEXT);
    }

    @Override
    public void layout(@Nonnull RenderContext ctx) {
        if (textNode == null) {
            textNode = new Text();
        }
        updateNode(ctx, textNode);
    }

    @Nonnull
    public CssRectangle2D getCssBoundsInLocal() {
        return new CssRectangle2D(getBoundsInLocal());
    }

    @Override
    public void reshapeInLocal(@Nonnull Transform transform) {
        Point2D o = getNonnull(ORIGIN).getConvertedValue();
        o = transform.transform(o);
        set(ORIGIN, new CssPoint2D(o));
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        set(ORIGIN, new CssPoint2D(x, y));
    }

    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Text();
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        Text tn = (Text) node;
        tn.setText(get(TEXT));
        tn.setX(getStyledNonnull(ORIGIN).getX().getConvertedValue());
        tn.setY(getStyledNonnull(ORIGIN).getY().getConvertedValue());
        tn.setBoundsType(TextBoundsType.VISUAL);
        applyHideableFigureProperties(ctx, node);
        applyTransformableFigureProperties(ctx, tn);
        applyTextableFigureProperties(ctx, tn);
        applyStrokableFigureProperties(ctx, tn);
        applyFillableFigureProperties(ctx, tn);
        applyCompositableFigureProperties(ctx, tn);
        applyTextFontableFigureProperties(ctx, tn);
        applyTextLayoutableFigureProperties(ctx, tn);
        applyStyleableFigureProperties(ctx, node);
        tn.applyCss();// really??
    }

    @Nonnull
    @Override
    public Connector findConnector(@Nonnull Point2D p, Figure prototype) {
        return new RectangleConnector(new BoundsLocator(getBoundsInLocal(), p));
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        if (textNode == null) {
            layout(new SimpleRenderContext());
        }
        return Shapes.awtShapeFromFX(textNode).getPathIterator(tx);
    }


}
