/*
 * @(#)ReadOnlyListIterator.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ReadOnlyListIterator<E> implements Iterator<E>, ListIterator<E>, Spliterator<E> {
    private final ReadOnlyList<E> list;
    private int index;
    @Nullable
    final Integer size;

    public ReadOnlyListIterator(@NonNull ReadOnlyList<E> list) {
        this(list, 0, null);
    }

    public ReadOnlyListIterator(ReadOnlyList<E> list, int index, @Nullable Integer size) {
        this.list = list;
        this.size = size;
        this.index = index;
    }

    @Override
    public boolean hasNext() {
        return index < getSize();
    }

    private int getSize() {
        return size == null ? list.size() : size;
    }

    @Override
    public E next() {
        return list.get(index++);
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public E previous() {
        return list.get(--index);
    }

    @Override
    public int nextIndex() {
        return index;
    }

    @Override
    public int previousIndex() {
        return index - 1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        Spliterator.super.forEachRemaining(action);
    }

    @Override
    public void set(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryAdvance(@Nullable Consumer<? super E> action) {
        Objects.requireNonNull(action, "action is null");
        if (index >= 0 && index < getSize()) {
            action.accept(list.get(index++));
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Spliterator<E> trySplit() {
        int lo = index, mid = (lo + getSize()) >>> 1;
        return (lo >= mid)
                ? null
                : new ReadOnlyListIterator<>(list, lo, index = mid);
    }

    @Override
    public long estimateSize() {
        return getSize() - index;
    }

    @Override
    public int characteristics() {
        return Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED;
    }
}
