/* @(#)AbstractLabelFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.List;
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
import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.FigureKey;
import org.jhotdraw8.draw.key.InsetsStyleableMapAccessor;
import org.jhotdraw8.draw.key.Point2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.SvgPathStyleableFigureKey;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.AWTPathBuilder;
import org.jhotdraw8.geom.FXPathBuilder;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;

/**
 * A Label that can be placed anywhere on a drawing.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractLabelFigure extends AbstractLeafFigure
        implements TextFillableFigure, FillableFigure, StrokeableFigure,
        FontableFigure, ConnectableFigure, PathIterableFigure {

    public final static DoubleStyleableFigureKey ORIGIN_X = new DoubleStyleableFigureKey("originX", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey ORIGIN_Y = new DoubleStyleableFigureKey("originY", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static Point2DStyleableMapAccessor ORIGIN = new Point2DStyleableMapAccessor("origin", ORIGIN_X, ORIGIN_Y);

    public final static DoubleStyleableFigureKey PADDING_BOTTOM = new DoubleStyleableFigureKey("paddingBottom", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey PADDING_LEFT = new DoubleStyleableFigureKey("paddingLeft", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey PADDING_RIGHT = new DoubleStyleableFigureKey("paddingRight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey PADDING_TOP = new DoubleStyleableFigureKey("paddingTop", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static InsetsStyleableMapAccessor PADDING = new InsetsStyleableMapAccessor("padding", PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, PADDING_LEFT);

    public final static SvgPathStyleableFigureKey SHAPE = new SvgPathStyleableFigureKey("shape", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), null);

    protected transient Bounds boundsInLocal;
    private Text textNode;

    public AbstractLabelFigure() {
        this(0, 0);
    }

    public AbstractLabelFigure(Point2D position) {
        this(position.getX(), position.getY());
        set(FILL, null);
        set(STROKE, null);
    }

    public AbstractLabelFigure(double x, double y) {
        set(ORIGIN, new Point2D(x, y));
    }

    @Override
    protected <T> void changed(Key<T> key, T oldv, T newv) {
        super.changed(key, oldv, newv);
        if ((key instanceof FigureKey)
                && ((FigureKey) key).getDirtyMask().containsOneOf(DirtyBits.LAYOUT)) {
            invalidateBounds();
        }
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        Group g = new Group();
        g.setAutoSizeChildren(false);
        Path p = new Path();
        Text text = new Text();
        g.getChildren().addAll(p, text);
        g.getProperties().put("path", p);
        g.getProperties().put("text", text);
        return g;
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return new RectangleConnector(new RelativeLocator(getBoundsInLocal(), p));
    }

    @Override
    public Bounds getBoundsInLocal() {
        if (boundsInLocal == null) {
            getLayoutBounds();
            Bounds b = textNode.getBoundsInLocal();
            Insets i = getStyled(PADDING);
            boundsInLocal = new BoundingBox(
                    b.getMinX() - i.getLeft(),
                    b.getMinY() - i.getTop(),
                    b.getWidth() + i.getLeft() + i.getRight(),
                    b.getHeight() + i.getTop() + i.getBottom());
        }
        return boundsInLocal;
    }

    /**
     * Returns the bounds of the node for layout calculations. These bounds
     * include the text of the node and the padding.
     *
     * @return the layout bounds
     */
    public Bounds getLayoutBounds() {
        if (textNode == null) {
            textNode = new Text();
        }
        updateTextNode(null, textNode);
        Bounds b = textNode.getLayoutBounds();
        Insets i = getStyled(PADDING);

        return new BoundingBox(
                b.getMinX() - i.getLeft(),
                b.getMinY() - i.getTop(),
                b.getWidth() + i.getLeft() + i.getRight(),
                textNode.getBaselineOffset() + i.getTop() + i.getBottom());
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        Text tn = new Text();
        tn.setText(getText(null));
        tn.setX(getStyled(ORIGIN).getX());
        tn.setY(getStyled(ORIGIN).getY());
        tn.setBoundsType(TextBoundsType.VISUAL);
        applyFontableFigureProperties(null, tn);
        return Shapes.awtShapeFromFX(tn).getPathIterator(tx);
    }

    protected abstract String getText(RenderContext ctx);

    protected void invalidateBounds() {
        boundsInLocal = null;
    }

    @Override
    public void layout() {
        invalidateBounds();
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        Bounds lb = getLayoutBounds();
        Insets i = getStyled(PADDING);
        set(ORIGIN, new Point2D(x + i.getLeft(), y + lb.getHeight() - i.getBottom()));
        invalidateBounds();
    }

    protected void updateGroupNode(RenderContext ctx, Group node) {

    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Group g = (Group) node;
        Path p = (Path) g.getProperties().get("path");
        Text t = (Text) g.getProperties().get("text");
        updateGroupNode(ctx, g);
        updatePathNode(ctx, p);
        updateTextNode(ctx, t);
    }

    protected void updatePathNode(RenderContext ctx, Path node) {
        applyFillableFigureProperties(node);
        applyStrokeableFigureProperties(node);

        String content = getStyled(SHAPE);
        if (content == null || content.trim().isEmpty()) {
            content = "M 0,0 1,0 1,1 0,1 Z";
        }
        Bounds b = getBoundsInLocal();

        try {
            AWTPathBuilder builder = new AWTPathBuilder();
            Shapes.buildFromSvgString(builder, content);
            Path2D.Double path = builder.get();
            FXPathBuilder builder2 = new FXPathBuilder();

            Transform tx = Transforms.createReshapeTransform(Geom.getBounds(path), getBoundsInLocal());
            AffineTransform at = Transforms.toAWT(tx);

            Shapes.buildFromPathIterator(builder2, path.getPathIterator(at));
            List<PathElement> elements = builder2.getElements();
            node.getElements().setAll(elements);

            node.setVisible(true);
        } catch (IOException ex) {
            node.setVisible(false);
            return;
        }
    }

    protected void updateTextNode(RenderContext ctx, Text tn) {
        tn.setText(getText(ctx));
        tn.setX(get(ORIGIN_X));
        tn.setY(get(ORIGIN_Y));
        applyTextFillableFigureProperties(tn);
        applyFontableFigureProperties(ctx, tn);
    }

}
