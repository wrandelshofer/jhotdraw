/*
 * @(#)ArrowPath.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyCollection;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents an arrow path through a graph.
 * <p>
 * Path elements are directed edges (arrows).
 *
 * @author Werner Randelshofer
 */
public class ArrowPath<E> {

    @NonNull
    private final ImmutableList<E> arrows;

    public ArrowPath(@NonNull ReadOnlyCollection<? extends E> elements) {
        this.arrows = ImmutableLists.ofCollection(elements);
    }

    public ArrowPath(@NonNull Collection<? extends E> elements) {
        this.arrows = ImmutableLists.ofCollection(elements);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ArrowPath<?> other = (ArrowPath<?>) obj;
        return Objects.equals(this.arrows, other.arrows);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.arrows);
        return hash;
    }

    @NonNull
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <VV> ArrowPath<VV> of(VV... vertices) {
        return new ArrowPath<>(ImmutableLists.of(vertices));
    }

    @NonNull
    public ImmutableList<E> getArrows() {
        return arrows;
    }

    public int size() {
        return arrows.size();
    }


    @NonNull
    @Override
    public String toString() {
        return "ArrowPath{" + arrows + '}';
    }


}
