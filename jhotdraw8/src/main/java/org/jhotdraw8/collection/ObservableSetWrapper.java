/* @(#)ObservableSetWrapper.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.util.Set;

/**
 * Wraps a {@link ReadableSet} in the {@link ObservableSet} API.
 * <p>
 * The underlying ReadableSet is referenced - not copied. This allows to pass a
 * ReadableSet to a client who does not understand the ReadableSet APi.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ObservableSetWrapper<E> extends SetWrapper<E> implements ObservableSet<E>{
    public ObservableSetWrapper(ReadableSet<E> backingSet) {
        super(backingSet);
    }

    @Override
    public void addListener(SetChangeListener<? super E> listener) {
        // empty
    }

    @Override
    public void removeListener(SetChangeListener<? super E> listener) {
        // empty
    }

    @Override
    public void addListener(InvalidationListener listener) {
        // empty
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        // empty
    }
}
