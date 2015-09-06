/* @(#)AbstractFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jhotdraw.beans.ListenerSupport;
import org.jhotdraw.beans.OptionalProperty;
import org.jhotdraw.beans.SimplePropertyBean;

/**
 * AbstractFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFigure extends SimplePropertyBean implements Figure {

    private final ObjectProperty<Figure> parent = new SimpleObjectProperty<Figure>(this, PARENT_PROPERTY);

    protected final ListenerSupport<InvalidationListener> invalidationListeners = new ListenerSupport();

    {
        properties.addListener((InvalidationListener) (Observable l) -> {
            invalidate();
        });
    }

    @Override
    public void addListener(InvalidationListener listener) {
        invalidationListeners.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListeners.removeListener(listener);
    }

    /** Whether the state of the figure is valid. */
    private boolean valid = true;

    @Override
    public ObjectProperty<Figure> parentProperty() {
        return parent;
    }

    /** This implementation always returns true. */
    @Override
    public boolean isSelectable() {
        return true;
    }

    /** Notifies all registered invalidation listeners. */
    public void fireInvalidated() {
        invalidationListeners.fire(l -> l.invalidated(this));
    }

    /** Marks the state of the figure as invalid. */
    protected final void invalidate() {
        if (valid) {
            valid = false;
            fireInvalidated();
        }
    }

    @Override
    public final boolean isValid() {
        return valid;
    }

    @Override
    public final void validate() {
        if (!valid) {
            updateState();
            valid = true;
        }
    }

    /** This method is invoked by validate when the state of the figure is
     * invalid. 
     * <p>
     * This implementation is empty. Subclasses which override this method
     * do not need to call super.
     */
    protected void updateState() {
        
    }
}
