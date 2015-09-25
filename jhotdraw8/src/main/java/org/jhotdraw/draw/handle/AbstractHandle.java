/* @(#)AbstractHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.handle;

import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.event.Listener;

/**
 * AbstractHandle.
 * @author Werner Randelshofer
 * @version $Id$
 * @param <F> the supported figure type
 */
public abstract class AbstractHandle<F extends Figure> implements Handle<F> {
    // ---
    // Fields
    // ---
    protected final F figure;
    protected final DrawingView dv;

    // ---
    // Constructors
    // ---
    public AbstractHandle(F figure, DrawingView dv) {
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
    public F getFigure() {
       return figure;
    }
}
