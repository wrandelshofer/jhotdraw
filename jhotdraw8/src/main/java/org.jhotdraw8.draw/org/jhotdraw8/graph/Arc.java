package org.jhotdraw8.graph;

/**
 * Data record for an arc in a directed graph.
 * <p>
 * Provides the start and end vertex of the arc, and the arrow data of the arc.
 * <p>
 * If a vertex has no predecessor, then start is null, and
 * method {@link #isRoot} returns true.
 *
 * @param <V> the vertex type
 * @param <A> the arrow data type
 */
public class Arc<V, A> {
    private final V start;
    private final V end;
    private final A arrow;

    public Arc(V start, V end, A arrow) {
        this.start = start;
        this.end = end;
        this.arrow = arrow;
    }

    public A getArrow() {
        return arrow;
    }

    public V getEnd() {
        return end;
    }

    public V getStart() {
        return start;
    }

    /**
     * Returns true if the vertex has no predecessor.
     *
     * @return true if start==null.
     */
    public boolean isRoot() {
        return start == null;
    }
}
