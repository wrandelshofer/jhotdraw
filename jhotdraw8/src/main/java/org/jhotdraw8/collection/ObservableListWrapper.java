package org.jhotdraw8.collection;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.Set;

/**
 * Wraps a {@link ReadableList} in the {@link ObservableList} API.
 * <p>
 * The underlying ReadableList is referenced - not copied. This allows to pass a
 * ReadableList to a client who does not understand the ReadableList APi.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ObservableListWrapper<E> extends ListWrapper<E> implements ObservableList<E> {
    public ObservableListWrapper(ReadableList<E> backingList) {
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

    @Override
    public boolean addAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setAll(Collection<? extends E> col) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(E... elements) {
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
