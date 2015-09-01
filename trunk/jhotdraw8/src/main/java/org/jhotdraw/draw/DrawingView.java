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
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import org.jhotdraw.beans.NonnullProperty;
import org.jhotdraw.beans.OptionalProperty;
import org.jhotdraw.draw.constrain.Constrainer;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.draw.handle.Handle;

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
    public final static String ZOOM_FACTOR_PROPERTY = "scaleFactor";
    /** The name of the drawing to view property. */
    public final static String DRAWING_TO_VIEW_PROPERTY = "drawingToView";
    /** The name of the constrainer property. */
    public final static String CONSTRAINER_PROPERTY = "constrainer";
    /** The name of the selection property. */
    public final static String SELECTION_PROPERTY = "selection";
    /** The name of the active handle property. */
    public final static String ACTIVE_HANDLE_PROPERTY = "activeHandle";


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
     * {@code Node} of the tool to its tool panel which is stacked on top of the 
     * drawing panel. It then invokes {@code toolsetDrawingView(this)}.
     * <p>
     * Setting a tool will remove the previous tool. The drawing view invokes
     * {@code tool.setDrawingView(null)} and then removes its {@code Node}
     * from its tool panel.
     *
     * @return the tool property, with {@code getBean()} returning this drawing view,
     * and {@code getName()} returning {@code TOOL_PROPERTY}.
     */
    OptionalProperty<Tool> toolProperty();

    /** The scale factor of the drawing view.
     * @return The zoom factor. The value is always greater than 0. 
    * Values larger than 1 cause a  magnification. 
    * Values between 0 and 1 causes a minification. 
    */
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

    /** The selected figures.
     * <p>
     * Note: Selection is a set. However, the sequence of the selection is important.
     */
    public ReadOnlySetProperty<Figure> selectionProperty();

    /** This property holds the transformation that is applied a drawing 
     * when it is displayed by the drawing view. This is typically a {@code Scale}
     * which uses the {@code zoomFactoryProperty} as the scale factor.
     @return 
     */
    public ReadOnlyObjectProperty<Transform> drawingToViewProperty();
    /** The active handle. 
     */
    OptionalProperty<Handle> activeHandleProperty();

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

    /** Finds the figure at the given view coordinates.
     * Figures are searched in Z-order from front to back.
     * @param vx x in view coordinates
     * @param vy y in view coordinates
     * @return A figure or empty
     */
    public Optional<Figure> findFigure(double vx, double vy);

    /** Finds the figure at the given view coordinates behind the given figure.
     * Figures are searched in Z-order from front to back.
     * @param vx x in view coordinates
     * @param vy y in view coordinates
     * @param figureInWay A figure which is in front of the desired figure
     * @return A figure or empty
     */
    public Optional<Figure> findFigureBehind(double vx, double vy, Figure figureInWay);
    /**
     * Returns all figures that lie within the specified
     * bounds given in view coordinates. The figures are returned in Z-order from back to front.
     * @param vx x in view coordinates
     * @param vy y in view coordinates
     * @param vwidth width in view coordinates
     * @param vheight height in view coordinates
     * @return A figure or empty
     */
    public List<Figure> findFigures(double vx, double vy, double vwidth, double vheight);
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
    default void setActiveHandle(Handle newValue) {
        activeHandleProperty().set(Optional.ofNullable(newValue));
    }

    default Optional<Handle> getActiveHandle() {
        return activeHandleProperty().get();
    }

    default void setZoomFactor(double newValue) {
        zoomFactorProperty().set(newValue);
    }

    default double getZoomFactor() {
        return zoomFactorProperty().get();
    }
    default ObservableSet<Figure> getSelectedFigures() {
        return selectionProperty().get();
    }

    default Transform getDrawingToView() {
        return drawingToViewProperty().get();
    }

    /** Converts view coordinates into drawing coordinates. */
    default Point2D viewToDrawing(Point2D view) {
        try {
            return getDrawingToView().inverseTransform(view);
        } catch (NonInvertibleTransformException ex) {
            throw new InternalError(ex);
        }
    }

    /** Converts drawing coordinates into view coordinates. */
    default Point2D drawingToView(Point2D drawing) {
        return getDrawingToView().transform(drawing);
    }

    /** Converts view coordinates into drawing coordinates. */
    default Point2D viewToDrawing(double vx, double vy) {
        try {
            return getDrawingToView().inverseTransform(vx, vy);
        } catch (NonInvertibleTransformException ex) {
            throw new InternalError(ex);
        }
    }

    /** Converts drawing coordinates into view coordinates. */
    default Point2D drawingToView(double dx, double dy) {
        return getDrawingToView().transform(dx, dy);
    }
    
    /** Returns the underlying drawing model. */
    DrawingModel getDrawingModel();
    
    // Handles
    /**
     * Gets compatible handles.
     * @return A collection containing the handle and all compatible handles.
     */
    public Collection<Handle> getCompatibleHandles(Handle handle);

}
