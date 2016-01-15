/* @(#)SimpleObservable.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.beans;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * SimpleObservable.
 *
 * @design.pattern SimpleObservable Observer, ConcreteSubject.
 * {@link SimpleObservable} is a concrete subject implementation of the
 * Observer pattern.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleObservable implements Observable {

    protected final ListenerSupport<InvalidationListener> invalidationListeners = new ListenerSupport<>();

    @Override
    public void addListener(InvalidationListener listener) {
         invalidationListeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
         invalidationListeners.remove(listener);
    }

    /** Notifies all registered invalidation listeners. */
    public void fireInvalidated() {
        invalidationListeners.fire(l->l.invalidated(this));
    }
}
