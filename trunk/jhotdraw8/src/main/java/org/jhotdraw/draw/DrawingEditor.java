/* @(#)DrawingEditor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SetProperty;
import org.jhotdraw.draw.tool.Tool;

/**
 * The DrawingEditor can be used to edit multiple {@code DrawingView}s with a 
 * single {@code Tool}.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DrawingEditor {
    // ---
    // properties
    // ---
    /** The drawing views associated with this editor.
     * @return  the property */
    ReadOnlySetProperty<DrawingView> drawingViewsProperty();
    
    /** The currently active drawing view.
     * @return the property
    */
    ObjectProperty<DrawingView> activeDrawingViewProperty();

    /** The currently active tool.
     * @return the property
    */
    ObjectProperty<Tool> activeToolProperty();

    // ---
    // convenience methods
    // ---
    /** Adds a drawing view to this editor.
     * @param drawingView the drawing view
    */
    default void addDrawingView(DrawingView drawingView) {
        drawingViewsProperty().add(drawingView);
    }
    /** Removes a drawing view to this editor.
     * @param drawingView the drawing view
    */
    default void removeDrawingView(DrawingView drawingView) {
        drawingViewsProperty().remove(drawingView);
    }
    
    /** Gets the active drawing view.
     * @return the active drawing view or empty
    */
    default DrawingView getActiveDrawingView() {
        return activeDrawingViewProperty().get();
    }

    /** Sets the active drawing view.
     * @param drawingView the drawing view or null if none is active
    */
    default void setActiveDrawingView(DrawingView drawingView) {
        activeDrawingViewProperty().set(drawingView);
    }

    /** Gets the active tool.
     * @return the active tool or empty
    */
    default Tool getActiveTool() {
        return activeToolProperty().get();
    }

    /** Sets the active tool.
     * @param tool the active tool or null if none is active
    */
    default void setActiveTool(Tool tool) {
        activeToolProperty().set(tool);
    }

    
}
