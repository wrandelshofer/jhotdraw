/*
 * @(#)ObservableListProxy.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.TransformationList;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * ObservableListProxy.
 *
 * @author Werner Randelshofer
 */
public class ObservableListProxy<A, B> extends TransformationList<A, B> {

    private final Function<A, B> toB;
    private final Function<B, A> toA;

    public ObservableListProxy(@NonNull ObservableList<B> source,
                               Function<A, B> toB, Function<B, A> toA) {
        super(source);
        this.toB = toB;
        this.toA = toA;
    }

    @Override
    protected void sourceChanged(ListChangeListener.Change<? extends B> c) {
        fireChange(new ChangeProxy<>(this, c, toA));

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
    public A get(int index) {
        return toA.apply(getSource().get(index));
    }

    @Override
    public int size() {
        return getSource().size();
    }

    @Override
    public A remove(int index) {
        return toA.apply(getSource().remove(index));
    }

    @Override
    public boolean remove(Object o) {
        @SuppressWarnings("unchecked") final A a = (A) o;
        return getSource().remove(toB.apply(a));
    }

    @Override
    public void add(int index, A e) {
        @SuppressWarnings("unchecked")
        ObservableList<B> source = (ObservableList<B>) getSource();
        source.add(index, toB.apply(e));
    }

    @Override
    public boolean add(A e) {
        @SuppressWarnings("unchecked")
        ObservableList<B> src = (ObservableList<B>) getSource();
        return src.add(toB.apply(e));
    }

    @Override
    public A set(int index, A e) {
        @SuppressWarnings("unchecked")
        ObservableList<B> src = (ObservableList<B>) getSource();
        return toA.apply(src.set(index, toB.apply(e)));
    }

    @Override
    public void remove(int from, int to) {
        @SuppressWarnings("unchecked")
        ObservableList<B> src = (ObservableList<B>) getSource();
        src.remove(from, to);
    }

    public static class ChangeProxy<A, B> extends ListChangeListener.Change<A> {

        private final ListChangeListener.Change<? extends B> change;
        private final Function<B, A> toA;
        @Nullable
        private int[] perm;

        public ChangeProxy(ObservableList<A> list, ListChangeListener.Change<? extends B> change, Function<B, A> toA) {
            super(list);
            this.change = change;
            this.toA = toA;
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

        @NonNull
        @Override
        public List<A> getRemoved() {
            @SuppressWarnings("unchecked")
            List<B> temp = (List<B>) change.getRemoved();
            ArrayList<A> list = new ArrayList<>(temp.size());
            for (B b : temp) {
                list.add(toA.apply(b));
            }
            return list;
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
