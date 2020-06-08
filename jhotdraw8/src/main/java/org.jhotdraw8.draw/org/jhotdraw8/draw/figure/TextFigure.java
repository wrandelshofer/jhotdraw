/*
 * @(#)TextFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.CssPoint2DStyleableKey;
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
    public final static CssPoint2DStyleableKey ORIGIN = new CssPoint2DStyleableKey("origin", new CssPoint2D(0, 0));

    private Text textNode;

    public TextFigure() {
        this(0, 0, "");
    }

    public TextFigure(@NonNull Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }

    public TextFigure(double x, double y, String text) {
        set(TEXT, text);
        set(ORIGIN, new CssPoint2D(x, y));
    }

    @NonNull
    @Override
    public Bounds getLayoutBounds() {
        // FIXME the text node should be computed during layout
        if (textNode == null) {
            layout(new SimpleRenderContext());
        }

        Bounds b = textNode.getLayoutBounds();
        return new BoundingBox(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @NonNull
    @Override
    public TextEditorData getTextEditorDataFor(Point2D pointInLocal, Node node) {
        return new TextEditorData(this, getLayoutBounds(), TEXT);
    }

    @Override
    public void layout(@NonNull RenderContext ctx) {
        if (textNode == null) {
            textNode = new Text();
        }
        updateNode(ctx, textNode);
    }

    @NonNull
    public CssRectangle2D getCssLayoutBounds() {
        return new CssRectangle2D(getLayoutBounds());
    }

    @Override
    public void reshapeInLocal(@NonNull Transform transform) {
        Point2D o = getNonNull(ORIGIN).getConvertedValue();
        o = transform.transform(o);
        set(ORIGIN, new CssPoint2D(o));
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        Bounds b = getLayoutBounds();
        reshapeInLocal(Transform.translate(x.getConvertedValue() - b.getMinX(), y.getConvertedValue() - b.getMinY()));
    }

    @NonNull
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Text();
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Text tn = (Text) node;
        tn.setX(getStyledNonNull(ORIGIN).getX().getConvertedValue());
        tn.setY(getStyledNonNull(ORIGIN).getY().getConvertedValue());
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

        // We must set the font before we set the text, so that JavaFx does not need to retrieve
        // the system default font, which on Windows requires that the JavaFx Toolkit is launched.
        tn.setText(get(TEXT));
    }

    @NonNull
    @Override
    public Connector findConnector(@NonNull Point2D p, Figure prototype) {
        return new RectangleConnector(new BoundsLocator(getLayoutBounds(), p));
    }

    @NonNull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        if (textNode == null) {
            layout(new SimpleRenderContext());
        }
        return Shapes.awtShapeFromFX(textNode).getPathIterator(tx);
    }


}
