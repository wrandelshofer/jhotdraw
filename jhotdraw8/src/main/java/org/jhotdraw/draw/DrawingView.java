/* @(#)DrawingView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SetProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import org.jhotdraw.beans.NonnullProperty;
import org.jhotdraw.beans.OptionalProperty;
import org.jhotdraw.draw.constrain.Constrainer;
import org.jhotdraw.draw.tool.Tool;

/**
 * A {@code DrawingView} can display a {@code Drawing}.
 * <p>
 * A {@code DrawingView} consists of the following layers:
 * <ul>
 * <li>Background. Displays a background behind the drawing.</li>
 * <li>Drawing. Displays the figures of the drawing.</li>
 * <li>Grid. Displays a grid.</li>
 * <li>Tools. Displays the handles used for editing figures.</li>
 * </ul>
 * 
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DrawingView {

    // ---
    // property names
    // ----
    /** The name of the drawing property. */
    public final static String DRAWING_PROPERTY = "drawing";
    /** The name of the tool property. */
    public final static String TOOL_PROPERTY = "tool";
    /** The name of the focused property. */
    public final static String FOCUSED_PROPERTY = "focused";
    /** The name of the scale factor property. */
    public final static String SCALE_FACTOR_PROPERTY = "scaleFactor";
    public final static String CONSTRAINER_PROPERTY = "constrainer";

    // ---
    // properties
    // ---
    /** The drawing. 
     * @return the drawing property, with {@code getBean()} returning this drawing view,
     and {@code getName()} returning {@code DRAWING_PROPERTY}.
     */
    NonnullProperty<Drawing> drawingProperty();

    /** The tool which currently edits this {@code DrawingView}.
     * <p>
     * When a tool is set on the drawing view, then drawing view adds the
     * node of the tool to its handle panel which is stacked on top of the 
     * drawing panel. 
     *
     * @return the tool property, with {@code getBean()} returning this drawing view,
     * and {@code getName()} returning {@code TOOL_PROPERTY}.
     */
    OptionalProperty<Tool> toolProperty();

    /** The scale factor of the drawing view. */
    DoubleProperty zoomFactorProperty();

    /** The constrainer. 
     * @return the constrainer property, with {@code getBean()} returning this drawing view,
     and {@code getName()} returning {@code CONSTRAINER_PROPERTY}.
     */
    NonnullProperty<Constrainer> constrainerProperty();

    /**
     * The focused property is set to true, when the DrawingView has input focus.
     * @return the focused property, with {@code getBean()} returning this drawing view,
     and {@code getName()} returning {@code FOCUSED_PROPERTY}.
     */
    public ReadOnlyBooleanProperty focusedProperty();

    /** The selected figures. */
    public ReadOnlySetProperty<Figure> selectionProperty();
    // ---
    // methods
    // ---
    /** Returns the {@code javafx.scene.Node} of the DrawingView. */
    public Node getNode();

    /** Puts a node for the specified figure into the drawing view.
     * The drawing view uses this node to build a scene graph.
     The node can not be part of multiple drawing views.
     <p>
     * By convention this method is only invoked by {@code Figure}.
     */
    public void putNode(Figure f, Node newNode);

    /** Gets the node which is used to render the specified figure by the drawing view.
     */
    public Node getNode(Figure f);

    // ---
    // convenience methods
    // ---

    default void setDrawing(Drawing newValue) {
        drawingProperty().set(newValue);
    }

    default Drawing getDrawing() {
        return drawingProperty().get();
    }

    default void setConstrainer(Constrainer newValue) {
        constrainerProperty().set(newValue);
    }

    default Constrainer getConstrainer() {
        return constrainerProperty().get();
    }

    default void setTool(Tool newValue) {
        toolProperty().set(Optional.ofNullable(newValue));
    }

    default Optional<Tool> getTool() {
        return toolProperty().get();
    }

    default void setZoomFactor(double newValue) {
        zoomFactorProperty().set(newValue);
    }

    default double getZoomFactor() {
        return zoomFactorProperty().get();
    }

    /** Converts view coordinates into drawing coordinates. */
    default Point2D viewToDrawing(Point2D view) {
        double f = 1.0 / getZoomFactor();
        return new Point2D(view.getX() * f, view.getY() * f);
    }

    /** Converts drawing coordinates into view coordinates. */
    default Point2D drawingToView(Point2D drawing) {
        double f = getZoomFactor();
        return new Point2D(drawing.getX() * f, drawing.getY() * f);
    }

    /** Converts view coordinates into drawing coordinates. */
    default Point2D viewToDrawing(double vx, double vy) {
        double f = 1.0 / getZoomFactor();
        return new Point2D(vx * f, vy * f);
    }

    /** Converts drawing coordinates into view coordinates. */
    default Point2D drawingToView(double dx, double dy) {
        double f = getZoomFactor();
        return new Point2D(dx * f, dy * f);
    }

    /** Finds the figure at the given view coordinates.
     * Figures are searched in Z-order from front to back.
     */
    public Optional<Figure> findFigure(double vx, double vy);

    /**
     * Returns all figures that lie within the specified
     * bounds given in view coordinates. The figures are returned in Z-order from back to front.
     */
    public List<Figure> findFigures(double vx, double vy, double vwidth, double vheight);

}
