/* @(#)AbstractObservable.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.beans;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * AbstractObservable.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class AbstractObservable implements Observable {

    private List<InvalidationListener> invalidationListeners;

    public void addListener(InvalidationListener listener) {
        if (invalidationListeners == null) {
            invalidationListeners = new CopyOnWriteArrayList<>();
        }
        invalidationListeners.add(listener);
    }

    protected void fireInvalidated() {
        fireInvalidated(this);
    }

    protected void fireInvalidated(Observable o) {
        if (invalidationListeners != null) {
            for (InvalidationListener l : invalidationListeners) {
                l.invalidated(o);
            }
        }
    }

    public void removeListener(InvalidationListener listener) {
        if (invalidationListeners != null) {
            invalidationListeners.remove(listener);
            if (invalidationListeners.isEmpty()) {
                invalidationListeners = null;
            }
        }
    }
}
