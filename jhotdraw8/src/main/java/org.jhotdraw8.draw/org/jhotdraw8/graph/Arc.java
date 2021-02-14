/*
 * @(#)Arc.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

/**
 * Data record for an arrow with associated data in a directed graph.
 * <p>
 * "Arc" is used as a synonym for arrow in some definitions for directed
 * graphs. In this design, "Arc" explicitly mans a data object that contains
 * the start and end vertices of and arrow and an associated data object.
 *
 * @param <V> the vertex type
 * @param <A> the arrow data type
 */
public class Arc<V, A> {
    private final @NonNull V start;
    private final @NonNull V end;
    private final @Nullable A data;

    public Arc(@NonNull V start, @NonNull V end, @Nullable A data) {
        Objects.requireNonNull(start, "start is null");
        Objects.requireNonNull(end, "end is null");
        this.start = start;
        this.end = end;
        this.data = data;
    }

    public @Nullable A getData() {
        return data;
    }

    public @NonNull V getEnd() {
        return end;
    }

    public @NonNull V getStart() {
        return start;
    }

    @Override
    public String toString() {
        return "Arc{" +
                "" + start +
                "->" + end +
                ", " + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Arc)) {
            return false;
        }
        Arc<?, ?> arc = (Arc<?, ?>) o;
        return Objects.equals(start, arc.start) &&
                end.equals(arc.end) &&
                Objects.equals(data, arc.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, data);
    }
}
