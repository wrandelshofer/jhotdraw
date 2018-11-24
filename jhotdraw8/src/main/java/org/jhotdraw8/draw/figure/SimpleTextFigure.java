/* @(#)SimpleTextFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;

import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.CssPoint2DStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.Shapes;

/**
 * {@code SimpleTextFigure} is a {@code FontableFigure} which supports stroking and
 * filling of the text.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleTextFigure extends AbstractLeafFigure
        implements StrokableFigure, FillableFigure, TransformableFigure, FontableFigure,
        TextableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        ConnectableFigure, PathIterableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Text";
    public final static CssPoint2DStyleableFigureKey ORIGIN = new CssPoint2DStyleableFigureKey("origin", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT), new CssPoint2D(0, 0));

    private Text textNode;

    public SimpleTextFigure() {
        this(0, 0, "");
    }

    public SimpleTextFigure(Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }

    public SimpleTextFigure(double x, double y, String text) {
        set(TEXT, text);
        set(ORIGIN, new CssPoint2D(x, y));
    }

    @Nonnull
    @Override
    public Bounds getBoundsInLocal() {
        if (textNode == null) {
            textNode = new Text();
        }
        updateNode(null, textNode);

        Bounds b = textNode.getBoundsInLocal();
        return new BoundingBox(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }
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
        applyHideableFigureProperties(node);
        applyTransformableFigureProperties(ctx, tn);
        applyTextableFigureProperties(tn);
        applyStrokableFigureProperties(tn);
        applyFillableFigureProperties(tn);
        applyCompositableFigureProperties(tn);
        applyFontableFigureProperties(ctx, tn);
        applyStyleableFigureProperties(ctx, node);
        tn.applyCss();// really??
    }

    @Nonnull
    @Override
    public Connector findConnector(@Nonnull Point2D p, Figure prototype) {
        return new RectangleConnector(new RelativeLocator(getBoundsInLocal(), p));
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        Text tn=new Text();
         tn.setText(get(TEXT));
        tn.setX(getStyledNonnull(ORIGIN).getX().getConvertedValue());
        tn.setY(getStyledNonnull(ORIGIN).getY().getConvertedValue());
        tn.setBoundsType(TextBoundsType.VISUAL);
        applyTextableFigureProperties(tn);
        applyFontableFigureProperties(null, tn);
        applyStyleableFigureProperties(null, tn);
        return Shapes.awtShapeFromFX(tn).getPathIterator(tx);
    }

}
