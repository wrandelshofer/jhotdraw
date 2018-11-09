package org.jhotdraw8.collection;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.stream.Stream;

/**
 * WritableArrayList supports most of the List API, but does not allow
 * to change the list in iterators, unless a writable iterator is requested,
 * so that it does not violate the contract of ReadableList.
 *
 * @param <E> the element type
 */
public class WritableArrayList<E> extends ArrayList<E> implements ReadableList<E> {
    public WritableArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public WritableArrayList() {
        super();
    }

    public WritableArrayList(@NotNull Collection<? extends E> c) {
        super(c);
    }

    @Override
    public Stream<E> stream() {
        return super.stream();
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator(int index) {
        return new ReadableListIterator<E>(this, index);
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return listIterator(0);
    }

    @NotNull
    public ListIterator<E> writableListIterator(int index) {
        return super.listIterator(index);
    }

    @NotNull
    public ListIterator<E> writableListIterator() {
        return super.listIterator();
    }

    @NotNull
    public Iterator<E> writableIterator() {
        return super.iterator();
    }
}
