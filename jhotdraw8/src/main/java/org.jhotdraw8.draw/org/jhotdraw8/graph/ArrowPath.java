/*
 * @(#)ArrowPath.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
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
 * Represents an arrow data path through a graph.
 * <p>
 * Path elements are data elements of directed edges (arrows).
 *
 * @param <A> the arrow data type
 * @author Werner Randelshofer
 */
public class ArrowPath<A> {

    @NonNull
    private final ImmutableList<A> arrows;

    public ArrowPath(@NonNull ReadOnlyCollection<? extends A> elements) {
        this.arrows = ImmutableLists.ofCollection(elements);
    }

    public ArrowPath(@NonNull Collection<? extends A> elements) {
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
    public ImmutableList<A> getArrows() {
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
