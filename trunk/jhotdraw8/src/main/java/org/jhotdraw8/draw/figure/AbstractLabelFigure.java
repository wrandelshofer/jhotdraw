/* @(#)AbstractLabelFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import static org.jhotdraw8.draw.figure.TextFigure.ORIGIN;
import static org.jhotdraw8.draw.figure.TextableFigure.TEXT;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.InsetsStyleableMapAccessor;
import org.jhotdraw8.draw.key.SvgPathStyleableFigureKey;
import org.jhotdraw8.draw.key.Point2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.FigureKey;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.text.Paintable;

/**
 * AbstractLabelFigure.
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
    public Node createNode(RenderContext drawingView) {
        Group g = new Group();
        g.setAutoSizeChildren(false);
        Region r = new Region();
        r.setScaleShape(true);
        // g.getChildren().add(r);
        Text text = new Text();
        g.getChildren().add(text);
        g.getProperties().put("region", r);
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

    protected abstract String getText(RenderContext ctx);

    private void invalidateBounds() {
        boundsInLocal = null;
    }

    @Override
    protected void invalidated(Key<?> key) {
        super.invalidated(key);
        if ((key instanceof FigureKey)
                && ((FigureKey) key).getDirtyMask().containsOneOf(DirtyBits.LAYOUT)) {
            invalidateBounds();
        }
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

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        Group g = (Group) node;
        Region r = (Region) g.getProperties().get("region");
        Text t = (Text) g.getProperties().get("text");
        updateRegionNode(ctx, r);
        updateTextNode(ctx, t);

        if (getStyled(FILL) != null || getStyled(STROKE) != null) {
            if (g.getChildren().size() != 2) {
                g.getChildren().setAll(r, t);
            }
        } else {
            if (g.getChildren().size() != 1) {
                g.getChildren().setAll(t);
            }
        }
    }

    private void updateRegionNode(RenderContext ctx, Region node) {
        String content = getStyled(SHAPE);
        SVGPath svgPath;
        if (content != null) {
            svgPath = new SVGPath();
            svgPath.setContent(content);
        } else {
            svgPath = null;
        }
        node.setShape(svgPath);

        Bounds b = getBoundsInLocal();
        node.resizeRelocate(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());

        Paint fillColor = Paintable.getPaint(getStyled(FILL));
        node.setBackground(fillColor == null ? null : new Background(new BackgroundFill(fillColor, null, null)));

        Paint strokeColor = Paintable.getPaint(getStyled(STROKE));
        double strokeWidth = getStyled(STROKE_WIDTH);
        if (strokeColor == null || strokeWidth == 0) {
            node.setBorder(Border.EMPTY);
        } else {
            BorderStrokeStyle bss = new BorderStrokeStyle(getStyled(STROKE_TYPE),
                    getStyled(STROKE_LINE_JOIN), getStyled(STROKE_LINE_CAP), getStyled(STROKE_MITER_LIMIT), getStyled(STROKE_DASH_OFFSET), getStyled(STROKE_DASH_ARRAY));
            node.setBorder(new Border(new BorderStroke(strokeColor,
                    bss, CornerRadii.EMPTY, new BorderWidths(strokeWidth))));
        }
    }

    protected void updateTextNode(RenderContext ctx, Text tn) {
        tn.setText(getText(ctx));
        tn.setX(get(ORIGIN_X));
        tn.setY(get(ORIGIN_Y));
        applyTextFillableFigureProperties(tn);
        applyFontableFigureProperties(ctx, tn);
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
}
