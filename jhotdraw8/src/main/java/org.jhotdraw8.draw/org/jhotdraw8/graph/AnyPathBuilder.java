/*
 * @(#)AnyPathBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.function.AddToSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Builder for creating any paths from a directed graph.
 * <p>
 * The builder searches for paths using a breadth-first search.<br>
 * Returns the first path that it finds.<br>
 * Returns nothing if there is no path.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 */
public class AnyPathBuilder<V, A> extends AbstractPathBuilder<V, A> {


    /**
     * Creates a new instance.
     *
     * @param graph a graph
     */
    public AnyPathBuilder(@NonNull DirectedGraph<V, A> graph) {
        this(graph::getNextVertices);
    }

    /**
     * Creates a new instance.
     *
     * @param nextNodesFunction Accessor function to next nodes in graph.
     */
    public AnyPathBuilder(@NonNull Function<V, Iterable<V>> nextNodesFunction) {
        super(nextNodesFunction);
    }

    /**
     * Enumerates all vertex paths from start to goal up to the specified maximal path length.
     *
     * @param start     the start vertex
     * @param goal      the goal predicate
     * @param maxLength the maximal length of a path
     * @return the enumerated paths
     */
    public @NonNull List<VertexPath<V>> findAllVertexPaths(@NonNull V start,
                                                           @NonNull Predicate<V> goal,
                                                           int maxLength) {
        List<MyBackLink<V, A>> backlinks = new ArrayList<>();
        searchAll(new MyBackLink<>(start, null, 1), goal,
                getNextNodesFunction(),
                backlinks, maxLength);
        List<VertexPath<V>> vertexPaths = new ArrayList<>(backlinks.size());
        Deque<V> path = new ArrayDeque<>();
        for (MyBackLink<V, A> list : backlinks) {
            path.clear();
            for (MyBackLink<V, A> backlink = list; backlink != null; backlink = backlink.parent) {
                path.addFirst(backlink.vertex);
            }
            vertexPaths.add(new VertexPath<V>(path));
        }
        return vertexPaths;
    }


    /**
     * Breadth-first-search.
     *
     * @param root      the starting point of the search
     * @param goal      the goal of the search
     * @param visited   a predicate with side effect. The predicate returns true
     *                  if the specified vertex has been visited, and marks the specified vertex
     *                  as visited.
     * @param maxLength the maximal path length
     * @return a back link on success, null on failure
     */
    public @Nullable BackLink<V, A> search(@NonNull V root,
                                           @NonNull Predicate<V> goal,
                                           @NonNull Function<V, Iterable<V>> nextNodesFunction,
                                           @NonNull AddToSet<V> visited,
                                           int maxLength) {
        Queue<MyBackLink<V, A>> queue = new ArrayDeque<>(16);
        MyBackLink<V, A> rootBackLink = new MyBackLink<>(root, null, maxLength);
        if (visited.add(root)) {
            queue.add(rootBackLink);
        }

        while (!queue.isEmpty()) {
            MyBackLink<V, A> node = queue.remove();
            if (goal.test(node.vertex)) {
                return node;
            }

            if (node.maxRemaining > 0) {
                for (V next : nextNodesFunction.apply(node.vertex)) {
                    if (visited.add(next)) {
                        MyBackLink<V, A> backLink = new MyBackLink<>(next, node, node.maxRemaining - 1);
                        queue.add(backLink);
                    }
                }
            }
        }

        return null;
    }

    private void searchAll(@NonNull MyBackLink<V, A> start, @NonNull Predicate<V> goal,
                           @NonNull Function<V, Iterable<V>> nextNodesFunction,
                           @NonNull List<MyBackLink<V, A>> backlinks, int maxDepth) {
        Deque<MyBackLink<V, A>> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            MyBackLink<V, A> current = stack.pop();
            if (goal.test(current.vertex)) {
                backlinks.add(current);
            }
            if (current.maxRemaining < maxDepth) {
                for (V v : nextNodesFunction.apply(current.vertex)) {
                    MyBackLink<V, A> newPath = new MyBackLink<>(v, current, current.maxRemaining + 1);
                    stack.push(newPath);
                }
            }
        }
    }

    private static class MyBackLink<VV, AA> extends BackLink<VV, AA> {

        final MyBackLink<VV, AA> parent;
        final VV vertex;
        final int maxRemaining;

        public MyBackLink(VV vertex, MyBackLink<VV, AA> parent, int depth) {
            this.vertex = vertex;
            this.parent = parent;
            this.maxRemaining = depth;
        }

        @Override
        BackLink<VV, AA> getParent() {
            return parent;
        }

        @Override
        VV getVertex() {
            return vertex;
        }
    }

}
