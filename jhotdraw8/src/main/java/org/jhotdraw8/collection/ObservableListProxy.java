/* @(#)ObservableListProxy.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Collection;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ObservableListProxy.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ObservableListProxy<E> extends TransformationList<E, E> {

    public ObservableListProxy(@Nonnull ObservableList<? extends E> source) {
        super(source);

    }

    @Override
    protected void sourceChanged(ListChangeListener.Change<? extends E> c) {
        fireChange(new ChangeProxy<>(this, c));

    }

    @Override
    public int getSourceIndex(int index) {
        return index;
    }
    
    // XXX mark as override in Java 9
    //@Override
    public int getViewIndex(int index) {
        return index;
    }

    @Override
    public E get(int index) {
        return getSource().get(index);
    }

    @Override
    public int size() {
        return getSource().size();
    }

    @Override
    public E remove(int index) {
        return getSource().remove(index);
    }

    @Override
    public boolean remove(Object o) {
        return getSource().remove(o);
    }

    @Override
    public void add(int index, E e) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        src.add(index, e);
    }

    @Override
    public boolean add(E e) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        return src.add(e);
    }

    @Override
    public E set(int index, E e) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        return src.set(index, e);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(E... elements) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        return src.addAll(elements);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean setAll(E... elements) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        return src.setAll(elements);
    }

    @Override
    public boolean addAll(Collection<? extends E> col) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        return src.addAll(col);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> col) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        return src.addAll(index, col);
    }

    @Override
    public boolean setAll(Collection<? extends E> col) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        return src.setAll(col);
    }

    @Override
    public boolean removeAll(Collection<?> col) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        return src.removeAll(col);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean removeAll(E... elements) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        return src.removeAll(elements);
    }

    @Override
    public boolean retainAll(Collection<?> col) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        return src.retainAll(col);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean retainAll(E... elements) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        return src.retainAll(elements);
    }

    @Override
    public void remove(int from, int to) {
        @SuppressWarnings("unchecked")
        ObservableList<E> src = (ObservableList<E>) getSource();
        src.remove(from, to);
    }

    /**
     * Notifies all listeners of a change
     *
     * @param from start of range
     * @param to end of range + 1
     */
    public void fireUpdated(int from, int to) {
        beginChange();
        for (int i = from; i < to; i++) {
            nextUpdate(i);
        }
        endChange();
    }

    static class ChangeProxy<E> extends ListChangeListener.Change<E> {

        private final ListChangeListener.Change<? extends E> change;
        @Nullable
        private int[] perm;

        public ChangeProxy(ObservableList<E> list, ListChangeListener.Change<? extends E> change) {
            super(list);
            this.change = change;
        }

        @Override
        public boolean next() {
            perm = null;
            return change.next();
        }

        @Override
        public void reset() {
            change.reset();
        }

        @Override
        public int getTo() {
            return change.getTo();
        }

        @Nonnull
        @Override
        public List<E> getRemoved() {
            @SuppressWarnings("unchecked")
            List<E> temp = (List<E>) change.getRemoved();
            return temp;
        }

        @Override
        public int getFrom() {
            return change.getFrom();
        }

        @Override
        public boolean wasUpdated() {
            return change.wasUpdated();
        }

        @Nullable
        @Override
        protected int[] getPermutation() {
            if (perm == null) {
                if (change.wasPermutated()) {
                    final int from = change.getFrom();
                    final int n = change.getTo() - from;
                    perm = new int[n];
                    for (int i = 0; i < n; i++) {
                        perm[i] = change.getPermutation(from + i);
                    }
                } else {
                    perm = new int[0];
                }
            }
            return perm;
        }

    }

}
