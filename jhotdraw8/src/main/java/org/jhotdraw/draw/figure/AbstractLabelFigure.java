/* @(#)TextFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import java.util.List;
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
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.RenderContext;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.handle.BoundsInLocalOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.HandleType;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.draw.handle.RotateHandle;
import org.jhotdraw.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw.draw.key.FigureKey;
import org.jhotdraw.draw.key.InsetsStyleableMapAccessor;
import org.jhotdraw.draw.key.SvgPathStyleableFigureKey;
import org.jhotdraw.draw.key.Point2DStyleableMapAccessor;
import org.jhotdraw.draw.locator.RelativeLocator;

/**
 * AbstractLabelFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractLabelFigure extends AbstractLeafFigure implements LabeledFigure, FillableFigure, StrokeableFigure, TextableFigure {

    public final static DoubleStyleableFigureKey ORIGIN_X = new DoubleStyleableFigureKey("originX", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey ORIGIN_Y = new DoubleStyleableFigureKey("originY", DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), 0.0);
    public final static Point2DStyleableMapAccessor ORIGIN = new Point2DStyleableMapAccessor("origin", ORIGIN_X, ORIGIN_Y);

    public final static DoubleStyleableFigureKey PADDING_TOP = new DoubleStyleableFigureKey("paddingTop", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey PADDING_RIGHT = new DoubleStyleableFigureKey("paddingRight", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey PADDING_BOTTOM = new DoubleStyleableFigureKey("paddingBottom", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey PADDING_LEFT = new DoubleStyleableFigureKey("paddingLeft", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), 0.0);
    public final static InsetsStyleableMapAccessor PADDING = new InsetsStyleableMapAccessor("padding", PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, PADDING_LEFT);
    private final static SVGPath defaultShape = new SVGPath();

    public final static SvgPathStyleableFigureKey SHAPE = new SvgPathStyleableFigureKey("shape", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.CONNECTION_LAYOUT), null);
    /**
     * The CSS type selector for a label object is {@code "Label"}.
     */
    public final static String TYPE_SELECTOR = "Label";

    private Text textNode;
    protected transient Bounds boundsInLocal;

    public AbstractLabelFigure() {
        this(0, 0);
    }

    public AbstractLabelFigure(Point2D position) {
        this(position.getX(), position.getY());
    }

    public AbstractLabelFigure(double x, double y) {
        set(ORIGIN, new Point2D(x, y));
    }

    /**
     * Returns the bounds of the node for layout calculations.
     * These bounds include the text of the node and the padding.
     * @return the layout bounds
     */
    public Bounds getLayoutBounds() {
        if (textNode == null) {
            textNode = new Text();
        }
        updateTextNode(null, textNode);
        Bounds b = textNode.getLayoutBounds();
        Insets i = getStyled(PADDING);
        return new BoundingBox(b.getMinX()-i.getLeft(),b.getMinY()-i.getTop(),
        b.getWidth()+i.getLeft()+i.getRight(),b.getHeight()+i.getTop()+i.getBottom());
    }

    @Override
    public Bounds getBoundsInLocal() {
        if (boundsInLocal == null) {
            Bounds b = getLayoutBounds();
            boundsInLocal = new BoundingBox(
                    b.getMinX() ,
                    b.getMinY() ,
                    b.getWidth() ,
                    b.getHeight());
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

    protected abstract String getText();

    protected void updateTextNode(RenderContext drawingView, Text tn) {
        tn.setText(getText());
        tn.setX(get(ORIGIN_X));
        tn.setY(get(ORIGIN_Y));
        applyLabeledFigureProperties(tn);
        applyTextableFigureProperties(tn);
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
        super.invalidated(key);
        if ((key instanceof FigureKey)
                && ((FigureKey) key).getDirtyMask().containsOneOf(DirtyBits.LAYOUT)) {
            invalidateBounds();
        }
    }

    @Override
    public void createHandles(HandleType handleType, DrawingView dv, List<Handle> list) {
        if (handleType == HandleType.SELECT) {
            list.add(new BoundsInLocalOutlineHandle(this));
        } else if (handleType == HandleType.MOVE) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_MOVE_OUTLINE));
            list.add(new MoveHandle(this, RelativeLocator.northEast()));
            list.add(new MoveHandle(this, RelativeLocator.northWest()));
            list.add(new MoveHandle(this, RelativeLocator.southEast()));
            list.add(new MoveHandle(this, RelativeLocator.southWest()));
        } else if (handleType == HandleType.RESIZE) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_MOVE_OUTLINE));
            list.add(new MoveHandle(this, RelativeLocator.northEast()));
            list.add(new MoveHandle(this, RelativeLocator.northWest()));
            list.add(new MoveHandle(this, RelativeLocator.southEast()));
            list.add(new MoveHandle(this, RelativeLocator.southWest()));
            if (this instanceof TransformableFigure) {
                list.add(new RotateHandle((TransformableFigure) this));
            }
        } else if (handleType == HandleType.TRANSFORM) {
            list.add(new BoundsInLocalOutlineHandle(this, Handle.STYLECLASS_HANDLE_TRANSFORM_OUTLINE));
            if (this instanceof TransformableFigure) {
                list.add(new RotateHandle((TransformableFigure) this));
            }
        }
    }
}
