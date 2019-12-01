/*
 * @(#)Arc.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

/**
 * Data record for an arrow with associated data in a directed graph
 * ("arrow record" is abbreviated to "arc").
 * <p>
 * Provides the start and end vertex of the arrow, and the data associated
 * to the arrow.
 *
 * @param <V> the vertex type
 * @param <A> the arrow data type
 */
public class Arc<V, A> {
    @NonNull
    private final V start;
    @NonNull
    private final V end;
    @Nullable
    private final A data;

    public Arc(@NonNull V start, @NonNull V end, @Nullable A data) {
        Objects.requireNonNull(start, "start is null");
        Objects.requireNonNull(end, "end is null");
        this.start = start;
        this.end = end;
        this.data = data;
    }

    @Nullable
    public A getData() {
        return data;
    }

    @NonNull
    public V getEnd() {
        return end;
    }

    @NonNull
    public V getStart() {
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
