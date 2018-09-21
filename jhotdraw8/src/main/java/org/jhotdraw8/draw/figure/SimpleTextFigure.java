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
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.Point2DStyleableFigureKey;
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
        implements StrokeableFigure, FillableFigure, TransformableFigure, FontableFigure,
        TextableFigure, HideableFigure, StyleableFigure, LockableFigure, CompositableFigure,
        ConnectableFigure, PathIterableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Text";
    public final static Point2DStyleableFigureKey ORIGIN = new Point2DStyleableFigureKey("origin", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT), new Point2D(0, 0));

    private Text textNode;

    public SimpleTextFigure() {
        this(0, 0, "");
    }

    public SimpleTextFigure(Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }

    public SimpleTextFigure(double x, double y, String text) {
        set(TEXT, text);
        set(ORIGIN, new Point2D(x, y));
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

    @Override
    public void reshapeInLocal(@Nonnull Transform transform) {
        Point2D o = get(ORIGIN);
        o = transform.transform(o);
        set(ORIGIN, o);
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        set(ORIGIN, new Point2D(x, y));
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
        tn.setX(getStyled(ORIGIN).getX());
        tn.setY(getStyled(ORIGIN).getY());
        tn.setBoundsType(TextBoundsType.VISUAL);
        applyHideableFigureProperties(node);
        applyTransformableFigureProperties(tn);
        applyTextableFigureProperties(tn);
        applyStrokeableFigureProperties(tn);
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
        tn.setX(getStyled(ORIGIN).getX());
        tn.setY(getStyled(ORIGIN).getY());
        tn.setBoundsType(TextBoundsType.VISUAL);
        applyTextableFigureProperties(tn);
        applyFontableFigureProperties(null, tn);
        applyStyleableFigureProperties(null, tn);
        return Shapes.awtShapeFromFX(tn).getPathIterator(tx);
    }

}
