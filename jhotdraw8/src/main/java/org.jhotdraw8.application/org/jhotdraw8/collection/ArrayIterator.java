/*
 * @(#)ArrayIterator.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ArrayIterator<E> implements Iterator<E>, ListIterator<E>, Spliterator<E>,
        Enumerator<E>, Consumer<E> {
    private final Object[] list;
    private int index;
    final int size;

    private E current;

    public ArrayIterator(@NonNull Object[] list) {
        this(list, 0, list.length);
    }

    public ArrayIterator(@NonNull Object[] list, int index, int size) {
        this.list = list;
        this.size = size;
        this.index = index;
    }

    @Override
    public boolean hasNext() {
        return index < getSize();
    }

    private int getSize() {
        return size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E next() {
        return current = (E) list[index++];
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E previous() {
        return current = (E) list[--index];
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

    @SuppressWarnings("unchecked")
    @Override
    public boolean tryAdvance(@Nullable Consumer<? super E> action) {
        Objects.requireNonNull(action, "action is null");
        if (index >= 0 && index < getSize()) {
            action.accept(current = (E) list[index++]);
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
                : new ArrayIterator<>(list, lo, index = mid);
    }

    @Override
    public long estimateSize() {
        return getSize() - index;
    }

    @Override
    public int characteristics() {
        return Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED;
    }

    @Override
    public boolean moveNext() {
        return tryAdvance(this);
    }

    @Override
    public E current() {
        return current;
    }

    @Override
    public void accept(E e) {
        current = e;
    }
}
