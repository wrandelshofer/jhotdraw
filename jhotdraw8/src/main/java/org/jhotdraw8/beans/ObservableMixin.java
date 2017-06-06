/* @(#)ObservableMixin.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.beans;

import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * ObservableMixin.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface ObservableMixin extends Observable {
     CopyOnWriteArrayList<InvalidationListener> getInvalidationListeners() ;

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
     */
    default void fireInvalidated() {
        for (InvalidationListener l : getInvalidationListeners()) {
            l.invalidated(this);
        }
    }
}
