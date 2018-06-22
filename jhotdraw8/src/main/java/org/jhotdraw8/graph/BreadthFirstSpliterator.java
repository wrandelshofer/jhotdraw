/* @(#)BreadthFirstSpliterator.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * BreadthFirstSpliterator.
 *
 * @author Werner Randelshofer
 * @param <V> the vertex type
 */
public class BreadthFirstSpliterator<V> extends AbstractSpliterator<V> {

    @Nullable
    private final Function<V, Iterable<V>> nextNodesFunction;
    @NonNull
    private final Queue<V> queue;
    @Nullable
    private final Predicate<V> visited;

    /**
     * Creates a new instance.
     *
     * @param nextNodesFunction the nextNodesFunction
     * @param root the root vertex
     */
    public BreadthFirstSpliterator(@NonNull Function<V, Iterable<V>> nextNodesFunction,@NonNull V root) {
        this(nextNodesFunction,root,new HashSet<>()::add);
    }

    /**
     * Creates a new instance.
     *
     * @param nextNodesFunction the nextNodesFunction
     * @param root the root vertex
     * @param visited a predicate with side effect. The predicate returns true
     * if the specified vertex has been visited, and marks the specified vertex
     * as visited.
     */
    public BreadthFirstSpliterator(@NonNull Function<V, Iterable<V>> nextNodesFunction, @NonNull V root, @NonNull Predicate<V> visited) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        Objects.requireNonNull(nextNodesFunction,"nextNodesFunction");
        Objects.requireNonNull(root,"root");
        Objects.requireNonNull(visited,"vistied");
        this.nextNodesFunction = nextNodesFunction;
        queue = new ArrayDeque<>(16);
        this.visited = visited;
        queue.add(root);
        visited.test(root);
    }


    @Override
    public boolean tryAdvance(@NonNull Consumer<? super V> action) {
        V current = queue.poll();
        if (current == null) {
            return false;
        }
        for (V next : nextNodesFunction.apply(current)) {
            if (visited.test(next)) {
                queue.add(next);
            }
        }
        action.accept(current);
        return true;
    }


}
