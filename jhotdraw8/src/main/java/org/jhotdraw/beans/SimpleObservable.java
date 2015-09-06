/* @(#)SimpleObservable.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.beans;

import java.util.ArrayList;
import java.util.LinkedList;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.value.ObservableValueBase;

/**
 * SimpleObservable.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleObservable implements Observable {

    protected final ListenerSupport<InvalidationListener> invalidationListeners = new ListenerSupport();

    @Override
    public void addListener(InvalidationListener listener) {
         invalidationListeners.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
         invalidationListeners.removeListener(listener);
    }

    /** Notifies all registered invalidation listeners. */
    public void fireInvalidated() {
        invalidationListeners.fire(l->l.invalidated(this));
    }
}
