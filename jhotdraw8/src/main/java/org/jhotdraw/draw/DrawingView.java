/* @(#)DrawingView.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.Optional;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
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
 * <li>Pages. Displays the pages of the drawing.</li>
 * <li>Drawing. Displays the figures of the drawing.</li>
 * <li>Handles. Displays the handles used for editing figures.</li>
 * <li>Grid. Displays a grid.</li>
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
     * and {@code getName()} returning {@code DRAWING_PROPERTY}.
     */
    NonnullProperty<Drawing> drawing();

    /** The tool which currently edits this {@code DrawingView}.
     * <p>
     * When a tool is set on the drawing view, then drawing view adds the
     * node of the tool to its handle panel which is stacked on top of the 
     * drawing panel. 
     *
     * @return the tool property, with {@code getBean()} returning this drawing view,
     * and {@code getName()} returning {@code TOOL_PROPERTY}.
     */
    OptionalProperty<Tool> tool();
    
    /** The scale factor of the drawing view. */
    DoubleProperty scaleFactor();
    
    /** Returns the {@code javafx.scene.Node} of the DrawingView. */
    public Node getNode();
    
    /** Puts a node for the specified figure into the drawing view.
     * The drawing view uses this node to build a scene graph.
     * The node can not be part of multiple drawing views.
     * <p>
     * By convention this method is only invoked by {@code Figure}.
     */
    public void putNode(Figure f, Node newNode);

    /** Gets the node which is used to render the specified figure by the drawing view.
     */
    public Node getNode(Figure f);

    /** The constrainer. 
     * @return the constrainer property, with {@code getBean()} returning this drawing view,
     * and {@code getName()} returning {@code CONSTRAINER_PROPERTY}.
     */
    NonnullProperty<Constrainer> constrainer();
    /**
     * The focused property is set to true, when the DrawingView has input focus.
     * @return the focused property, with {@code getBean()} returning this drawing view,
     * and {@code getName()} returning {@code FOCUSED_PROPERTY}.
     */
    public ReadOnlyBooleanProperty focusedProperty();
    // ---
    // convenience methods
    // ---
    default void setDrawing(Drawing newValue) {
        drawing().set(newValue);
    }

    default Drawing getDrawing() {
        return drawing().get();
    }
    default void setConstrainer(Constrainer newValue) {
        constrainer().set(newValue);
    }

    default Constrainer getConstrainer() {
        return constrainer().get();
    }

    default void setTool(Tool newValue) {
        tool().set(Optional.ofNullable(newValue));
    }

    default Optional<Tool> getTool() {
        return tool().get();
    }

    

}
