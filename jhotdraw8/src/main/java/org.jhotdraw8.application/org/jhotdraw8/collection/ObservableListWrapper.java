/*
 * @(#)ObservableListWrapper.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collection;

/**
 * Wraps a {@link ReadOnlyList} in the {@link ObservableList} API.
 * <p>
 * The underlying ReadOnlyList is referenced - not copied. This allows to pass a
 * ReadOnlyList to a client who does not understand the ReadOnlyList APi.
 *
 * @author Werner Randelshofer
 */
public class ObservableListWrapper<E> extends ListWrapper<E> implements ObservableList<E> {
    public ObservableListWrapper(ReadOnlyList<E> backingList) {
        super(backingList);
    }

    @Override
    public void addListener(ListChangeListener<? super E> listener) {
        // empty
    }

    @Override
    public void removeListener(ListChangeListener<? super E> listener) {
        // empty
    }

    @SafeVarargs
    @Override
    public final boolean addAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @Override
    public final boolean setAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setAll(Collection<? extends E> col) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @Override
    public final boolean removeAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    @Override
    public final boolean retainAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int from, int to) {
        throw new UnsupportedOperationException();
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
