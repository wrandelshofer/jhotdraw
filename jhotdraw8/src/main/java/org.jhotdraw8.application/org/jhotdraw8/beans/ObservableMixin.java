/*
 * @(#)ObservableMixin.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ObservableMixin.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ObservableMixin extends Observable {

    CopyOnWriteArrayList<InvalidationListener> getInvalidationListeners();

    @Override
    default void addListener(InvalidationListener listener) {
        getInvalidationListeners().add(listener);
    }

    @Override
    default void removeListener(InvalidationListener listener) {
        getInvalidationListeners().remove(listener);
    }

    /**
     * Notifies all registered invalidation listeners.
     *
     * @param o observable. Will not be used. Listeners are notified with "this"
     *          as the observable.
     */
    default void fireInvalidated(Observable o) {
        invalidated();
        final CopyOnWriteArrayList<InvalidationListener> listeners = getInvalidationListeners();
        if (!listeners.isEmpty()) {
            for (InvalidationListener l : listeners) {
                l.invalidated(this);
            }
        }
    }

    default void fireInvalidated() {
        fireInvalidated(this);
    }

    /**
     * The method {@code invalidated()} can be overridden to receive
     * invalidation notifications. This is the preferred option in
     * {@code Objects} defining the property, because it requires less memory.
     * <p>
     * The default implementation is empty.
     */
    default void invalidated() {
    }
}
