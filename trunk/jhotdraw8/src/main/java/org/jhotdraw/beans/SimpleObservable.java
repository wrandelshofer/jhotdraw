/* @(#)SimpleObservable.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.beans;

import java.util.LinkedList;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * SimpleObservable.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleObservable implements Observable {

    /** Nullable. */
    private LinkedList<InvalidationListener> invalidationListeners;

    @Override
    public void addListener(InvalidationListener listener) {
        if (invalidationListeners == null) {
            invalidationListeners = new LinkedList<>();
        }
        invalidationListeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListeners.remove(listener);
        if (invalidationListeners != null) {
            if (invalidationListeners.isEmpty()) {
                invalidationListeners = null;
            }
        }
    }

    public void fireInvalidated() {
        if (invalidationListeners != null) {
            // We clone the list here. This way, listeners can remove themselves
            // from the list during event handling.
            for (InvalidationListener l : new LinkedList<>(invalidationListeners)) {
                l.invalidated(this);
            }
        }

    }
}
