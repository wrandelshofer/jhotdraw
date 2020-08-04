/*
 * @(#)ListTransformContentBinding.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.binding;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

class ListTransformContentBinding<D, S> implements ListChangeListener<S> {
    @NonNull
    private final ObservableList<D> dest;
    @NonNull
    private final ObservableList<S> source;
    @NonNull
    private final Function<S, D> toDest;
    @Nullable
    private final Consumer<D> destOnRemove;


    ListTransformContentBinding(@NonNull ObservableList<D> dest, @NonNull ObservableList<S> source, @NonNull Function<S, D> toDest, @Nullable Consumer<D> destOnRemove) {
        this.dest = dest;
        this.source = source;
        this.toDest = toDest;
        this.destOnRemove = destOnRemove;
        if (destOnRemove != null) {
            for (D d : dest) {
                destOnRemove.accept(d);
            }
        }
        dest.clear();
        for (S s : source) {
            D d = toDest.apply(s);
            dest.add(d);
        }
    }

    @Override
    public void onChanged(Change<? extends S> change) {
        while (change.next()) {
            int from = change.getFrom();
            int to = change.getTo();
            List<? extends S> s = change.getList();
            if (change.wasPermutated()) {
                remove(dest, from, to);
                List<D> d = new ArrayList<>(to - from);
                for (int i = from; i < to; i++) {
                    d.add(toDest.apply(s.get(i)));
                }
                dest.addAll(from, d);
            } else {
                if (change.wasRemoved() && change.wasAdded()) {
                    System.err.println("REMOVED and ADDED! " + change);
                }

                if (change.wasRemoved()) {
                    remove(dest, from, from + change.getRemovedSize());
                }
                if (change.wasAdded()) {
                    List<D> d = new ArrayList<>(to - from);
                    for (int i = from; i < to; i++) {
                        d.add(toDest.apply(s.get(i)));
                    }
                    dest.addAll(from, d);
                }
            }
        }
    }

    private void remove(ObservableList<D> dest, int from, int to) {
        if (destOnRemove != null) {
            for (int i = from; i < to; i++) {
                destOnRemove.accept(dest.get(i));
            }
        }
        dest.remove(from, to);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(dest);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof ListTransformContentBinding) {
            final ListTransformContentBinding<?, ?> that = (ListTransformContentBinding<?, ?>) obj;
            return this.dest == that.dest && this.source == that.source;
        }
        return false;
    }

}


