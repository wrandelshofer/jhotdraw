/* @(#)EdgePath.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ReadOnlyCollection;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents an edge path through a graph.
 * <p>
 * Path elements are directed edges (edges) or undirected edges (arcs).
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EdgePath<E> {

    @Nonnull
    private final ImmutableList<E> edges;

    public EdgePath(@Nonnull ReadOnlyCollection<? extends E> elements) {
        this.edges = ImmutableList.ofCollection(elements);
    }

    public EdgePath(@Nonnull Collection<? extends E> elements) {
        this.edges = ImmutableList.ofCollection(elements);
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
        final EdgePath<?> other = (EdgePath<?>) obj;
        if (!Objects.equals(this.edges, other.edges)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.edges);
        return hash;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <VV> EdgePath<VV> of(VV... vertices) {
        return new EdgePath<>(ImmutableList.of(vertices));
    }

    @Nonnull
    public ImmutableList<E> getEdges() {
        return edges;
    }

    @Nonnull
    @Override
    public String toString() {
        return "EdgePath{" + edges + '}';
    }


}
