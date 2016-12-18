/* @(#)AbstractDrawingViewInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.draw.inspector;

import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.model.DrawingModel;

/**
 * AbstractDrawingInspector.
 * @author Werner Randelshofer
 */
public abstract class AbstractDrawingViewInspector implements Inspector {
    protected DrawingView drawingView;

    @Override
    public void setDrawingView(DrawingView newValue) {
        DrawingView oldValue = drawingView;
        this.drawingView = newValue;
        onDrawingViewChanged(oldValue, newValue);
    }
    protected DrawingModel getDrawingModel() {
        return drawingView.getModel();
    }

    /** Can be overridden by subclasses. This implementation is empty. 
     * 
     * @param oldValue the old drawing view
     * @param newValue the new drawing view
     */
    protected void onDrawingViewChanged(DrawingView oldValue, DrawingView newValue) {
        
    }
}
