/*
 * @(#)BreadthFirstSpliterator.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.AbstractEnumeratorSpliterator;
import org.jhotdraw8.util.function.AddToSet;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Function;

/**
 * BreadthFirstSpliterator.
 *
 * @param <V> the vertex type
 * @author Werner Randelshofer
 */
public class BreadthFirstSpliterator<V> extends AbstractEnumeratorSpliterator<V> {

    private final @NonNull Function<V, Iterable<V>> nextFunction;
    private final @NonNull Deque<V> deque;
    private final AddToSet<V> visited;

    /**
     * Creates a new instance.
     *
     * @param nextFunction the nextFunction
     * @param root         the root vertex
     */
    public BreadthFirstSpliterator(@NonNull Function<V, Iterable<V>> nextFunction, @NonNull V root) {
        this(nextFunction, root, new HashSet<>()::add);
    }

    /**
     * Creates a new instance.
     *
     * @param nextFunction the nextFunction
     * @param root         the root vertex
     */
    public BreadthFirstSpliterator(@NonNull Function<V, Iterable<V>> nextFunction, @NonNull V root, @NonNull AddToSet<V> visited) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        Objects.requireNonNull(nextFunction, "nextFunction is null");
        Objects.requireNonNull(root, "root is null");
        this.nextFunction = nextFunction;
        deque = new ArrayDeque<>(16);
        this.visited = visited;
        if (visited.add(root)) {
            deque.add(root);
        }
    }

    @Override
    public boolean moveNext() {
        current = deque.pollFirst();
        if (current == null) {
            return false;
        }
        for (V next : nextFunction.apply(current)) {
            if (visited.add(next)) {
                deque.addLast(next);
            }
        }
        return true;
    }
}
