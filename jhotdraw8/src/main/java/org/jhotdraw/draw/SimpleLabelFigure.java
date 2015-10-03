/* @(#)TextFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.SimpleFigureKey;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;
import static org.jhotdraw.draw.shape.TextFigure.ORIGIN;

/**
 * TextFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleLabelFigure extends AbstractLeafFigure implements TextHolderFigure, LabelFigure {

    public final static Key<Point2D> ORIGIN = new SimpleFigureKey<>("origin", Point2D.class, DirtyMask.of(DirtyBits.NODE),new Point2D(0, 0));
    /**
     * The CSS type selector for a label object is {@code "Label"}.
     */
    public final static String TYPE_SELECTOR = "Label";

    private Text textNode;

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
        if (textNode == null) {
            textNode = new Text();
        }
        updateNode(null, textNode);

        Bounds b = textNode.getLayoutBounds();
        return new BoundingBox(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshape(Transform transform) {
        Point2D o = get(ORIGIN);
        o = transform.transform(o);
        set(ORIGIN, o);
    }
    @Override
    public void reshape(double x, double y, double width, double height) {
        set(ORIGIN, new Point2D(x, y));
    }
    
    @Override
    public Node createNode(RenderContext drawingView) {
        return new Text();
    }

    @Override
    public void updateNode(RenderContext drawingView, Node node) {
        Text tn = (Text) node;
        tn.setText(get(TEXT));
        tn.setX(get(ORIGIN).getX());
        tn.setY(get(ORIGIN).getY());
        tn.setBoundsType(TextBoundsType.VISUAL);
        applyFigureProperties(tn);
        applyTextProperties(tn);
        applyLabelProperties(tn);
    }

    @Override
    public boolean isLayoutable() {
        return false;
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return new ChopRectangleConnector();
    }

    @Override
    public void layout() {
        // empty!
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

}
