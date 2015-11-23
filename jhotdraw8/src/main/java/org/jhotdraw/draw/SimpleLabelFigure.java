/* @(#)TextFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
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
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.key.FigureKey;
import org.jhotdraw.draw.key.InsetsStyleableFigureKey;
import org.jhotdraw.draw.key.Point2DStyleableFigureKey;
import org.jhotdraw.draw.key.SVGPathStyleableFigureKey;

/**
 * TextFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleLabelFigure extends AbstractLeafFigure implements TextableFigure, LabelFigure, TransformableFigure, StyleableFigure, LockableFigure, FillableFigure, StrokeableFigure {

    public final static Point2DStyleableFigureKey ORIGIN = new Point2DStyleableFigureKey("origin", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), new Point2D(0, 0));
    public final static InsetsStyleableFigureKey PADDING = new InsetsStyleableFigureKey("padding", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), new Insets(4, 4, 4, 4));
    private final static SVGPath defaultShape = new SVGPath();

    {
        defaultShape.setContent("M0,0 L10,0 L10,10 L0,10 Z");
    }
    public final static SVGPathStyleableFigureKey SHAPE = new SVGPathStyleableFigureKey("shape", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), defaultShape);
    /**
     * The CSS type selector for a label object is {@code "Label"}.
     */
    public final static String TYPE_SELECTOR = "Label";

    private Text textNode;
    private transient Bounds boundsInLocal;

    public SimpleLabelFigure() {
        this(0, 0, "");
    }

    public SimpleLabelFigure(Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }

    public SimpleLabelFigure(double x, double y, String text) {
        set(TEXT, text);
        set(ORIGIN, new Point2D(x, y));
    }

    @Override
    public Bounds getBoundsInLocal() {
        if (boundsInLocal == null) {
            if (textNode == null) {
                textNode = new Text();
            }
            updateTextNode(null, textNode);

            Bounds b = textNode.getLayoutBounds();
            Insets i = getStyled(PADDING);
            boundsInLocal = new BoundingBox(b.getMinX() - i.getLeft(), b.getMinY() - i.getTop(), b.getWidth() + i.getLeft() + i.getRight(), b.getHeight() + i.getTop() + i.getBottom());
        }
        return boundsInLocal;
    }

    private void invalidateBounds() {
        boundsInLocal = null;
    }

    @Override
    public void reshape(Transform transform) {
        Point2D o = get(ORIGIN);
        o = transform.transform(o);
        set(ORIGIN, o);
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        set(ORIGIN, new Point2D(x, y + height));
        invalidateBounds();
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        Group g = new Group();
        g.setAutoSizeChildren(false);
        Region r = new Region();
        g.getChildren().add(r);
        g.getChildren().add(new Text());
        r.setScaleShape(true);
        return g;
    }

    @Override
    public void updateNode(RenderContext drawingView, Node node) {
        Group g = (Group) node;
        Region r = (Region) g.getChildren().get(0);
        Text t = (Text) g.getChildren().get(1);

        applyTransformableFigureProperties(g);
        updateRegionNode(drawingView, r);
        updateTextNode(drawingView, t);
    }

    private void updateRegionNode(RenderContext drawingView, Region node) {
        node.setShape(getStyled(SHAPE));
        
        Bounds b = getBoundsInLocal();
        node.resizeRelocate(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
        
        Paint fillColor = getStyled(FILL_COLOR);
        node.setBackground(fillColor == null ? null : new Background(new BackgroundFill(fillColor, null, null)));
        
        Paint strokeColor = getStyled(STROKE_COLOR);
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

    private void updateTextNode(RenderContext drawingView, Text tn) {
        tn.setText(get(TEXT));
        Point2D origin = get(ORIGIN);
        tn.setX(origin.getX());
        tn.setY(origin.getY());
        applyTextHolderProperties(tn);
        applyLabelProperties(tn);
    }

    @Override
    public boolean isLayoutable() {
        return false;
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return new ChopRectangleConnector(this);
    }

    @Override
    public void layout() {
        // empty!
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    protected void invalidated(Key<?> key) {
        if ((key instanceof FigureKey)
                && ((FigureKey) key).getDirtyMask().containsOneOf(DirtyBits.LAYOUT)) {
            invalidateBounds();
        }
    }

}
