/* @(#)Figure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import static org.jhotdraw.draw.FigureKeys.*;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import org.jhotdraw.beans.PropertyBean;
import static org.jhotdraw.draw.FigureKeys.FILL;
import static org.jhotdraw.draw.FigureKeys.SMOOTH;
import static org.jhotdraw.draw.FigureKeys.STROKE;
import static org.jhotdraw.draw.FigureKeys.STROKE_DASH_ARRAY;
import static org.jhotdraw.draw.FigureKeys.STROKE_DASH_OFFSET;
import static org.jhotdraw.draw.FigureKeys.STROKE_LINE_CAP;
import static org.jhotdraw.draw.FigureKeys.STROKE_LINE_JOIN;
import static org.jhotdraw.draw.FigureKeys.STROKE_MITER_LIMIT;
import static org.jhotdraw.draw.FigureKeys.STROKE_TYPE;
import static org.jhotdraw.draw.FigureKeys.STROKE_WIDTH;

/**
 * A {@code Figure} is an editable element of a {@link Drawing}.
 * <p>
 * A figure typically has a visual representation, such as a rectangle or a line.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Figure extends PropertyBean, Observable {

    /** The child figures. 
     * <p>
     * If a child is added to a figure, figure must set itself set as the parent
     * of the child immediately after the child has been added.
     * <p>
     * If a child is removed from a figure, figure must set parent to null
     * immediately before the child is removed.
     * @return the children of the figure
     */
    ListProperty<Figure> children();

    /** Adds a new child to the figure. */
    default void add(Figure newChild) {
        children().add(newChild);
    }

    /** Removes a child from the figure. */
    default void remove(Figure child) {
        children().remove(child);
    }

    /** The parent figure.
     * <p>
     * @return parent figure or empty, if the figure is the root.
     * @see #children
     */
    ObjectProperty<Optional<Figure>> parent();

    /** Returns the parent figure.
     * @return parent figure or empty, if the figure is the root.  */
    default Optional<Figure> getParent() {
        return parent().get();
    }

    /** The rectangular bounds that should be used for layout calculations
     * for this figure.
     * @return the layout bounds
     */
    public Rectangle2D getLayoutBounds();

    /** Attempts to change the layout bounds of the figure.
     * <p>
     * Width and height are ignored, if the figure is not resizable.
     * <p>
     * If the layout bounds of the figure changes, it fires an invalidation event.
     * @param x desired x-position
     * @param y desired y-position
     * @param width desired width
     * @param height desired height
     */
    default void reshape(double x, double y, double width, double height) {
        Rectangle2D oldBounds = getLayoutBounds();
        Rectangle2D newBounds = new Rectangle2D(x, y, width, height);

        double sx = newBounds.getWidth() / oldBounds.getWidth();
        double sy = newBounds.getHeight() / oldBounds.getHeight();

        Affine reshape = new Affine();
        Affine tx = new Affine();
        tx.appendTranslation(-oldBounds.getMinX(), -oldBounds.getMinY());
        if (!Double.isNaN(sx) && !Double.isNaN(sy)
                && !Double.isInfinite(sx) && !Double.isInfinite(sy)
                && (sx != 1d || sy != 1d)
                && !(sx < 0.0001) && !(sy < 0.0001)) {
            reshape.append(tx);
            tx.setToIdentity();
            tx.scale(sx, sy);
            reshape.append(tx);
            tx.setToIdentity();
        }
        tx.translate(newBounds.getMinX(), newBounds.getMinY());
        reshape.append(tx);
        reshape(reshape);
    }

    /** Attempts to change the layout bounds of the figure.
     * <p>
     * The figure may choose to only partially perform the transformation.
     * <p>
     * If the layout bounds of the figure changes, it fires an invalidation event.
     *
     * @param transform the desired transformation
     */
    void reshape(Transform transform);

    /** This method is invoked by {@code DrawingView}, when it needs a node
     * to create a scene graph for a figure.
     * <p>
     * A typical implementation should look like this:
     * <pre>{@code
     * public void putNode(DrawingView v) {
     *    v.putNode(this, new ...desired subclass of Node...());
     * }
     * }</pre>
     @param drawingView 
     */
    void putNode(DrawingView drawingView);

    /** This method is invoked by {@code DrawingView}, when it needs to update
     * the node which represents the scene graph in the figure.
     * <p>
     * A figure which is composed from other figures, can add the nodes of
     * of these figures to its node.
     * <pre>{@code
     * public void updateNode(DrawingView v, Node n) {
     *    ObservableList<Node> group = ((Group) n).getChildren();
     *    group.clear();
     *    for (Figure child : children()) {
     *       group.add(v.getNode(child));
     *    }
     * }
     * }</pre>
     @param drawingView 
     */
    void updateNode(DrawingView drawingView, Node node);

    /** Updates a regular node. */
    default void updateNodeProperties(Node node) {
        node.setBlendMode(get(BLEND_MODE));
        node.setEffect(get(EFFECT));
        node.setOpacity(get(OPACITY));
        node.setRotate(get(ROTATE));
        node.setRotationAxis(get(ROTATION_AXIS));
        node.setScaleX(get(SCALE_X));
        node.setScaleY(get(SCALE_Y));
        node.setScaleZ(get(SCALE_Z));
    }
    /** Updates a shape node. */
    default void updateShapeProperties(Shape shape) {
        updateNodeProperties(shape);
        shape.setFill(get(FILL));
        shape.setSmooth(get(SMOOTH));
        shape.setStrokeDashOffset(get(STROKE_DASH_OFFSET));
        shape.setStrokeLineCap(get(STROKE_LINE_CAP));
        shape.setStrokeLineJoin(get(STROKE_LINE_JOIN));
        shape.setStrokeMiterLimit(get(STROKE_MITER_LIMIT));
        shape.setStroke(get(STROKE));
        shape.setStrokeType(get(STROKE_TYPE));
        shape.setStrokeWidth(get(STROKE_WIDTH));
        shape.getStrokeDashArray().clear();
        for (double dash : get(STROKE_DASH_ARRAY)) {
            shape.getStrokeDashArray().add(dash);
        }
    }
    /** Updates a text node. */
    default void updateTextProperties(Text text) {
        updateNodeProperties(text);
        
        text.setFont(get(FONT));
        text.setFontSmoothingType(get(FONT_SMOOTHING_TYPE));
        text.setLineSpacing(get(LINE_SPACING));
        text.setStrikethrough(get(STRIKETHROUGH));
        text.setTextAlignment(get(TEXT_ALIGNMENT));
        text.setTextOrigin(get(TEXT_ORIGIN));
        text.setUnderline(get(UNDERLINE));
        text.setWrappingWidth(get(WRAPPING_WIDTH));
        
        text.setFill(get(TEXT_FILL));
        text.setSmooth(get(TEXT_SMOOTH));
        text.setStrokeDashOffset(get(TEXT_STROKE_DASH_OFFSET));
        text.setStrokeLineCap(get(TEXT_STROKE_LINE_CAP));
        text.setStrokeLineJoin(get(TEXT_STROKE_LINE_JOIN));
        text.setStrokeMiterLimit(get(TEXT_STROKE_MITER_LIMIT));
        text.setStroke(get(TEXT_STROKE));
        text.setStrokeType(get(TEXT_STROKE_TYPE));
        text.setStrokeWidth(get(TEXT_STROKE_WIDTH));
        text.getStrokeDashArray().clear();
        for (double dash : get(TEXT_STROKE_DASH_ARRAY)) {
            text.getStrokeDashArray().add(dash);
        }
    }
    
}
