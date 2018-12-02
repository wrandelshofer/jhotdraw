/* @(#)ObservableSetWrapper.java
 * Copyright © 2018 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 * Wraps a {@link ReadOnlySet} in the {@link ObservableSet} API.
 * <p>
 * The underlying ReadOnlySet is referenced - not copied. This allows to pass a
 * ReadOnlySet to a client who does not understand the ReadOnlySet APi.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ObservableSetWrapper<E> extends SetWrapper<E> implements ObservableSet<E>{
    public ObservableSetWrapper(ReadOnlySet<E> backingSet) {
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
