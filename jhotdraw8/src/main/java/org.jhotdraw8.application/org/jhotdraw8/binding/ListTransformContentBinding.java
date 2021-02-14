/*
 * @(#)ListTransformContentBinding.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
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

class ListTransformContentBinding<D, S> {
    private final @NonNull ObservableList<D> dest;
    private final @NonNull ObservableList<S> source;
    private final @NonNull Function<S, D> toDest;
    private final @Nullable Function<D, S> toSource;
    private final @Nullable Consumer<D> destOnRemove;
    private final @Nullable Consumer<S> sourceOnRemove;

    private int isChanging;

    private final ListChangeListener<S> sourceChangeListener = this::onSourceChanged;
    private final ListChangeListener<D> destChangeListener = this::onDestChanged;


    ListTransformContentBinding(@NonNull ObservableList<D> dest, @NonNull ObservableList<S> source, @NonNull Function<S, D> toDest, @Nullable Function<D, S> toSource, @Nullable Consumer<D> destOnRemove, @Nullable Consumer<S> sourceOnRemove) {
        this.dest = dest;
        this.source = source;
        this.toDest = toDest;
        this.toSource = toSource;
        this.destOnRemove = destOnRemove;
        this.sourceOnRemove = sourceOnRemove;

        isChanging++;
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
        isChanging--;
    }

    public ListChangeListener<S> getSourceChangeListener() {
        return sourceChangeListener;
    }

    public ListChangeListener<D> getDestChangeListener() {
        return destChangeListener;
    }

    private void onSourceChanged(ListChangeListener.Change<? extends S> change) {
        if (isChanging++ == 0) {
            while (change.next()) {
                int from = change.getFrom();
                int to = change.getTo();
                List<? extends S> s = change.getList();
                if (change.wasPermutated()) {
                    remove(dest, from, to, destOnRemove);
                    List<D> d = new ArrayList<>(to - from);
                    for (int i = from; i < to; i++) {
                        d.add(toDest.apply(s.get(i)));
                    }
                    dest.addAll(from, d);
                } else {
                    if (change.wasRemoved()) {
                        remove(dest, from, from + change.getRemovedSize(), destOnRemove);
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
        isChanging--;
    }

    private void onDestChanged(ListChangeListener.Change<? extends D> change) {
        Function<D, S> myToSource = this.toSource;
        if (myToSource == null) {
            return;
        }

        if (isChanging++ == 0) {
            while (change.next()) {
                int from = change.getFrom();
                int to = change.getTo();
                List<? extends D> d = change.getList();
                if (change.wasPermutated()) {
                    remove(source, from, to, sourceOnRemove);
                    List<S> s = new ArrayList<>(to - from);
                    for (int i = from; i < to; i++) {
                        s.add(myToSource.apply(d.get(i)));
                    }
                    source.addAll(from, s);
                } else {
                    if (change.wasRemoved()) {
                        remove(source, from, from + change.getRemovedSize(), sourceOnRemove);
                    }
                    if (change.wasAdded()) {
                        List<S> s = new ArrayList<>(to - from);
                        for (int i = from; i < to; i++) {
                            s.add(toSource.apply(d.get(i)));
                        }
                        source.addAll(from, s);
                    }
                }
            }
        }
        isChanging--;
    }

    private <E> void remove(ObservableList<E> list, int from, int to, Consumer<E> onRemove) {
        if (onRemove != null) {
            for (int i = from; i < to; i++) {
                onRemove.accept(list.get(i));
            }
        }
        list.remove(from, to);
    }


}


