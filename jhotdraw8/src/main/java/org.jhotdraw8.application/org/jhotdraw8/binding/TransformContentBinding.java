package org.jhotdraw8.binding;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class TransformContentBinding<D, S> implements ListChangeListener<S> {
    private final ObservableList<D> dest;
    private final ObservableList<S> source;
    private final Function<S, D> toDest;


    TransformContentBinding(ObservableList<D> dest, ObservableList<S> source, Function<S, D> toDest) {
        this.dest = dest;
        this.source = source;
        this.toDest = toDest;
    }

    @Override
    public void onChanged(Change<? extends S> change) {
        while (change.next()) {
            int from = change.getFrom();
            int to = change.getTo();
            List<? extends S> s = change.getList();
            if (change.wasPermutated()) {
                dest.remove(from, to);
                List<D> d = new ArrayList<>(from - to);
                for (int i = from; i < to; i++) {
                    d.add(toDest.apply(s.get(i)));
                }
                dest.addAll(from, d);
            } else {
                if (change.wasRemoved()) {
                    dest.remove(from, from + change.getRemovedSize());
                }
                if (change.wasAdded()) {
                    List<D> d = new ArrayList<>(from - to);
                    for (int i = from; i < to; i++) {
                        d.add(toDest.apply(s.get(i)));
                    }
                    dest.addAll(from, d);
                }
            }
        }
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

        if (obj instanceof TransformContentBinding) {
            final TransformContentBinding<?, ?> that = (TransformContentBinding<?, ?>) obj;
            return this.dest == that.dest && this.source == that.source;
        }
        return false;
    }

}


