/* @(#)VertexPath.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents a path through a graph.
 * <p>
 * Path elements are vertices.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class VertexPath<V> {

    private final List<V> elements;

    public VertexPath(Collection<V> elements) {
        this.elements = Collections.unmodifiableList(new ArrayList<>(elements));
    }

    public List<V> getVertices() {
        return elements;
    }

    @Override
    public String toString() {
        return "VertexPath{" + elements + '}';
    }



}
