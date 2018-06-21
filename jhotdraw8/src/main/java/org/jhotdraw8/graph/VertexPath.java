/* @(#)VertexPath.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a vertex path through a graph.
 * <p>
 * Path elements are nextArrows.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class VertexPath<V> {

    private final List<V> vertices;

    public VertexPath(Collection<V> elements) {
        this.vertices = Collections.unmodifiableList(new ArrayList<>(elements));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VertexPath<?> other = (VertexPath<?>) obj;
        if (!Objects.equals(this.vertices, other.vertices)) {
            return false;
        }
        return true;
    }

    public List<V> getVertices() {
        return vertices;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.vertices);
        return hash;
    }

    @Override
    public String toString() {
        return "VertexPath{" + vertices + '}';
    }

    /**
     * Creates a new VertexPath with the specified nextArrows.
     *
     * @param <VV> the vertex type
     * @param vertices the nextArrows
     * @return the vertex path
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <VV> VertexPath<VV> of(VV... vertices) {
        return new VertexPath<>(Arrays.asList(vertices));
    }

}
