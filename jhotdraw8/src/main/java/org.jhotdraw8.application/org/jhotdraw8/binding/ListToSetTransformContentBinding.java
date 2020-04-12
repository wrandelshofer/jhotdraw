package org.jhotdraw8.binding;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import java.util.function.Function;

class ListToSetTransformContentBinding<D, S> implements SetChangeListener<S> {
    private final ObservableList<D> dest;
    private final ObservableSet<S> source;
    private final Function<S, D> toDest;


    ListToSetTransformContentBinding(ObservableList<D> dest, ObservableSet<S> source, Function<S, D> toDest) {
        this.dest = dest;
        this.source = source;
        this.toDest = toDest;
        dest.clear();
        for (S s : source) {
            D d = toDest.apply(s);
            dest.add(d);
        }
    }

    @Override
    public void onChanged(Change<? extends S> change) {
        if (change.wasRemoved()) {
            dest.remove(toDest.apply(change.getElementRemoved()));
        }
        if (change.wasAdded()) {
            dest.add(toDest.apply(change.getElementAdded()));
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

        if (obj instanceof ListToSetTransformContentBinding) {
            final ListToSetTransformContentBinding<?, ?> that = (ListToSetTransformContentBinding<?, ?>) obj;
            return this.dest == that.dest && this.source == that.source;
        }
        return false;
    }

}


