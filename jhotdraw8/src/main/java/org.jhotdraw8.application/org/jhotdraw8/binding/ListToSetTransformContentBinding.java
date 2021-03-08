/*
 * @(#)ListToSetTransformContentBinding.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.binding;

import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Binds the content of a list to a set.
 */
class ListToSetTransformContentBinding<D, S> implements SetChangeListener<S> {
    private final ObservableList<D> dest;
    private final ObservableSet<S> source;
    private final Function<S, D> toDest;
    private final Consumer<D> disposeDest;


    /**
     * @param dest
     * @param source
     * @param toDest may only be null, if this instance is used for unbinding!
     */
    ListToSetTransformContentBinding(@NonNull ObservableList<D> dest, @NonNull ObservableSet<S> source, @Nullable Function<S, D> toDest) {
        this(dest, source, toDest, null);
    }

    /**
     * @param dest
     * @param source
     * @param toDest may only be null, if this instance is used for unbinding!
     */
    ListToSetTransformContentBinding(@NonNull ObservableList<D> dest, @NonNull ObservableSet<S> source,
                                     @Nullable Function<S, D> toDest,
                                     @Nullable Consumer<D> disposeDest) {
        this.dest = dest;
        this.source = source;
        this.toDest = toDest == null ? s -> null : toDest;
        this.disposeDest = disposeDest;
        if (toDest != null) {
            dest.clear();
            for (S s : source) {
                D d = toDest.apply(s);
                dest.add(d);
            }
        }
    }

    @Override
    public void onChanged(Change<? extends S> change) {
        if (change.wasRemoved()) {
            int i = dest.indexOf(toDest.apply(change.getElementRemoved()));
            if (i != -1) {
                D removed = dest.get(i);
                dest.remove(i);
                if (disposeDest != null) {
                    disposeDest.accept(removed);
                }
            }
        }
        if (change.wasAdded()) {
            dest.add(toDest.apply(change.getElementAdded()));
        }
    }

    @Override
    public int hashCode() {
        // Identity Hash Code is not based on content of list.
        return System.identityHashCode(dest);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof ListToSetTransformContentBinding) {
            final ListToSetTransformContentBinding<?, ?> that = (ListToSetTransformContentBinding<?, ?>) obj;
            // Compare identity of collections and not their content.
            return this.dest == that.dest && this.source == that.source;
        }
        return false;
    }

}


