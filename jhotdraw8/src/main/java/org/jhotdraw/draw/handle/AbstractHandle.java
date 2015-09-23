/* @(#)AbstractHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.handle;

import javafx.scene.Node;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.event.Listener;

/**
 * AbstractHandle.
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractHandle implements Handle {
    // ---
    // Fields
    // ---
    protected final Figure figure;
    protected final DrawingView dv;

    // ---
    // Constructors
    // ---
    public AbstractHandle(Figure figure, DrawingView dv) {
        this.figure = figure;
        this.dv = dv;
    }

    // ---
    // Behavior
    // ---
    @Override
    public final void dispose() {
    }

    @Override
    public Figure getFigure() {
       return figure;
    }
}
