/* @(#)EdgePath.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an edge path through a graph.
 * <p>
 Path elements are directed edges (edges) or undirected edges (arcs).
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EdgePath<E> {

    @NonNull
    private final List<E> edges;

    public EdgePath(@NonNull Collection<E> elements) {
        this.edges = Collections.unmodifiableList(new ArrayList<>(elements));
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
    
    @SafeVarargs @SuppressWarnings("varargs")
    public static<VV> EdgePath<VV> of(VV... vertices) {
        return new EdgePath<>(Arrays.asList(vertices));
    }

    @NonNull
    public List<E> getEdges() {
        return edges;
    }

    @NonNull
    @Override
    public String toString() {
        return "EdgePath{" + edges + '}';
    }



}
