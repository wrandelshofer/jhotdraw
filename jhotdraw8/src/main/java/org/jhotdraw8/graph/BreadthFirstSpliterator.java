/* @(#)BreadthFirstSpliterator.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    @Nonnull
    private final Function<V, Iterable<V>> nextNodesFunction;
    @Nonnull
    private final Deque<V> deque;
    @Nonnull
    private final Predicate<V> visited;

    /**
     * Creates a new instance.
     *
     * @param nextNodesFunction the nextNodesFunction
     * @param root the root vertex
     */
    public BreadthFirstSpliterator(@Nonnull Function<V, Iterable<V>> nextNodesFunction,@Nonnull V root) {
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
    public BreadthFirstSpliterator(@Nonnull Function<V, Iterable<V>> nextNodesFunction, @Nonnull V root, @Nonnull Predicate<V> visited) {
        super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
        Objects.requireNonNull(nextNodesFunction,"nextNodesFunction");
        Objects.requireNonNull(root,"root");
        Objects.requireNonNull(visited,"vistied");
        this.nextNodesFunction = nextNodesFunction;
        deque = new ArrayDeque<>(16);
        this.visited = visited;
        deque.add(root);
        visited.test(root);
    }


    @Override
    public boolean tryAdvance(@Nonnull Consumer<? super V> action) {
        V current = deque.pollFirst();
        if (current == null) {
            return false;
        }
        for (V next : nextNodesFunction.apply(current)) {
            if (visited.test(next)) {
                deque.addLast(next);
            }
        }
        action.accept(current);
        return true;
    }


}
