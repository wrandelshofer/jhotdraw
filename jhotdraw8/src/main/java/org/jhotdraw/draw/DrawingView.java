/*
 * @(#)DrawingView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.Collection;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import org.jhotdraw.beans.NonnullProperty;
import org.jhotdraw.draw.constrain.Constrainer;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.draw.handle.Handle;

/**
 * A {@code DrawingView} can display a {@code Drawing} in a JavaFX scene graph.
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
public interface DrawingView extends RenderContext {

    // ---
    // property names
    // ----
    /**
     * The name of the drawingModel property.
     */
    public final static String DRAWING_MODEL_PROPERTY = "drawingModel";
    /**
     * The name of the tool property.
     */
    public final static String TOOL_PROPERTY = "tool";
    /**
     * The name of the focused property.
     */
    public final static String FOCUSED_PROPERTY = "focused";
    /**
     * The name of the scale factor property.
     */
    public final static String ZOOM_FACTOR_PROPERTY = "scaleFactor";
    /**
     * The name of the constrainer property.
     */
    public final static String CONSTRAINER_PROPERTY = "constrainer";
    /**
     * The name of the selection property.
     */
    public final static String SELECTION_PROPERTY = "selection";
    /**
     * The name of the active handle property.
     */
    public final static String ACTIVE_HANDLE_PROPERTY = "activeHandle";
    /**
     * The name of the active layer property.
     */
    public final static String ACTIVE_LAYER_PROPERTY = "activeLayer";

    // ---
    // properties
    // ---
    /**
     * The drawing model.
     *
     * @return the drawing model property, with {@code getBean()} returning this
     * drawing view, and {@code getName()} returning {@code DRAWING_PROPERTY}.
     */
    NonnullProperty<DrawingModel> drawingModelProperty();
    /**
     * The active layer of the drawing.
     *
     * @return the active layer of the drawing. Returns null if the drawing
     * has no layers or no layer has been activated.
     */
    ObjectProperty<Layer> activeLayerProperty();

    /**
     * The tool which currently edits this {@code DrawingView}.
     * <p>
     * When a tool is set on the drawing view, then drawing view adds the
     * {@code Node} of the tool to its tool panel which is stacked on top of the
     * drawing panel. It then invokes {@code toolsetDrawingView(this)}.
     * <p>
     * Setting a tool will remove the previous tool. The drawing view invokes
     * {@code tool.setDrawingView(null)} and then removes its {@code Node} from
     * its tool panel.
     *
     * @return the tool property, with {@code getBean()} returning this drawing
     * view, and {@code getName()} returning {@code TOOL_PROPERTY}.
     */
    ObjectProperty<Tool> toolProperty();

    /**
     * The scale factor of the drawing view.
     *
     * @return The zoom factor. The value is always greater than 0. Values
     * larger than 1 cause a magnification. Values between 0 and 1 causes a
     * minification.
     */
    DoubleProperty zoomFactorProperty();

    /**
     * The constrainer.
     *
     * @return the constrainer property, with {@code getBean()} returning this
     * drawing view, and {@code getName()} returning
     * {@code CONSTRAINER_PROPERTY}.
     */
    NonnullProperty<Constrainer> constrainerProperty();

    /**
     * The focused property is set to true, when the DrawingView has input
     * focus.
     *
     * @return the focused property, with {@code getBean()} returning this
     * drawing view, and {@code getName()} returning {@code FOCUSED_PROPERTY}.
     */
    public ReadOnlyBooleanProperty focusedProperty();

    /**
     * The selected figures.
     * <p>
     * Note: Selection is a set. However, the sequence of the selection is
     * important.
     *
     * @return a list of the selected figures
     */
    public ReadOnlySetProperty<Figure> selectionProperty();

    /**
     * The active handle.
     *
     * @return the active handle if present
     */
    ObjectProperty<Handle> activeHandleProperty();

    // ---
    // methods
    // ---
    /**
     * Returns the {@code javafx.scene.Node} of the DrawingView.
     *
     * @return a node
     */
    public Node getNode();

    /**
     * Gets the node which is used to render the specified figure by the drawing
     * view.
     *
     * @param f The figure
     * @return The node associated to the figure
     */
    public Node getNode(Figure f);

    /**
     * Finds the figure at the given view coordinates. Figures are searched in
     * Z-order from front to back. Only considers figures in editable {@code Layer}s.
     *
     * @param vx x in view coordinates
     * @param vy y in view coordinates
     * @return A figure or empty
     */
    public Figure findFigure(double vx, double vy);

    /**
     * Finds the figure at the given view coordinates behind the given figure.
     * Figures are searched in Z-order from front to back. Only considers figures in editable {@code Layer}s.
     *
     * @param vx x in view coordinates
     * @param vy y in view coordinates
     * @param figureInWay A figure which is in front of the desired figure
     * @return A figure or empty
     */
    public Figure findFigureBehind(double vx, double vy, Figure figureInWay);

    /**
     * Returns all figures that lie within the specified bounds given in view
     * coordinates. The figures are returned in Z-order from back to front.
     * Only considers figures in editable {@code Layer}s.
     *
     * @param vx x in view coordinates
     * @param vy y in view coordinates
     * @param vwidth width in view coordinates
     * @param vheight height in view coordinates
     * @return A figure or empty
     */
    public List<Figure> findFiguresInside(double vx, double vy, double vwidth, double vheight);
    /**
     * Returns all figures that intersect the specified bounds given in view
     * coordinates. The figures are returned in Z-order from back to front.
     * Only considers figures in editable {@code Layer}s.
     *
     * @param vx x in view coordinates
     * @param vy y in view coordinates
     * @param vwidth width in view coordinates
     * @param vheight height in view coordinates
     * @return A figure or empty
     */
    public List<Figure> findFiguresIntersecting(double vx, double vy, double vwidth, double vheight);
    // ---
    // convenience methods
    // ---
    /**
     * Finds the figure at the given view coordinates. Figures are searched in
     * Z-order from front to back. Only considers figures in editable {@code Layer}s.
     *
     * @param v point in view coordinates
     * @return A figure or empty
     */
    default Figure findFigure(Point2D v) {
        return findFigure(v.getX(),v.getY());
    }
    /**
     * Returns all figures that intersect the specified bounds given in view
     * coordinates. The figures are returned in Z-order from back to front.
     * Only considers figures in editable {@code Layer}s.
     *
     * @param v rectangle in view coordinates
     * @return A figure or empty
     */
    default List<Figure> findFiguresIntersecting(Rectangle2D v) {
        return findFiguresIntersecting(v.getMinX(),v.getMinY(),v.getWidth(),v.getHeight());
    }

    default void setDrawing(Drawing newValue) {
        drawingModelProperty().get().setRoot(newValue);
    }

    default Drawing getDrawing() {
        return drawingModelProperty().get().getRoot();
    }

    default void setConstrainer(Constrainer newValue) {
        constrainerProperty().set(newValue);
    }

    default Constrainer getConstrainer() {
        return constrainerProperty().get();
    }

    default void setTool(Tool newValue) {
        toolProperty().set(newValue);
    }

    default Tool getTool() {
        return toolProperty().get();
    }

    default void setActiveHandle(Handle newValue) {
        activeHandleProperty().set(newValue);
    }

    default Handle getActiveHandle() {
        return activeHandleProperty().get();
    }
    default void setActiveLayer(Layer newValue) {
        activeLayerProperty().set(newValue);
    }

    default Layer getActiveLayer() {
        return activeLayerProperty().get();
    }

    default void setZoomFactor(double newValue) {
        zoomFactorProperty().set(newValue);
    }

    default double getZoomFactor() {
        return zoomFactorProperty().get();
    }

    default ObservableSet<Figure> getSelectedFigures() {
        return selectionProperty();
    }

    /** Returns the drawing to view transformation. */
    Transform getDrawingToView();
    /** Returns the view to drawing transformation. */
    Transform getViewToDrawing();

    /**
     * Converts view coordinates into drawing coordinates.
     *
     * @param view a point in view coordinates
     * @return the corresponding point in drawing coordinates
     */
    default Point2D viewToDrawing(Point2D view) {
        return getViewToDrawing().transform(view);
    }

    /**
     * Converts drawing coordinates into view coordinates.
     *
     * @param drawing a point in drawing coordinates
     * @return the corresponding point in view coordinates
     */
    default Point2D drawingToView(Point2D drawing) {
        return getDrawingToView().transform(drawing);
    }

    /**
     * Converts view coordinates into drawing coordinates.
     *
     * @param vx the x coordinate of a point in view coordinates
     * @param vy the y coordinate of a point in view coordinates
     * @return the corresponding point in drawing coordinates
     */
    default Point2D viewToDrawing(double vx, double vy) {
            return getViewToDrawing().transform(vx, vy);
    }

    /**
     * Converts drawing coordinates into view coordinates.
     *
     * @param dx the x coordinate of a point in drawing coordinates
     * @param dy the y coordinate of a point in drawing coordinates
     * @return the corresponding point in view coordinates
     */
    default Point2D drawingToView(double dx, double dy) {
        return getDrawingToView().transform(dx, dy);
    }

    /**
     * Returns the underlying drawing model.
     * @return a drawing model
     */
    DrawingModel getDrawingModel();

    // Handles
    /**
     * Gets compatible handles.
     *
     * @param handle a handle
     * @return A collection containing the handle and all compatible handles.
     */
    public Collection<Handle> getCompatibleHandles(Handle handle);
    
    

}
