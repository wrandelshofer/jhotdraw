/*
 * @(#)IntAnyPathBuilder.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.util.function.AddToIntSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

/**
 * Builder for creating any paths from a directed graph.
 * <p>
 * The builder searches for paths using a breadth-first search.<br>
 * Returns the first path that it finds.<br>
 * Returns nothing if there is no path.
 *
 * @author Werner Randelshofer
 */
public class IntAnyPathBuilder extends AbstractIntPathBuilder {


    /**
     * Creates a new instance.
     *
     * @param graph a graph
     */
    public IntAnyPathBuilder(@NonNull IntDirectedGraph graph) {
        this(graph::getNextVertices);
    }

    /**
     * Creates a new instance.
     *
     * @param nextNodesFunction Accessor function to next nodes in graph.
     */
    public IntAnyPathBuilder(@NonNull Function<Integer, Spliterator.OfInt> nextNodesFunction) {
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
    public @NonNull List<VertexPath<Integer>> findAllVertexPaths(int start,
                                                                 @NonNull IntPredicate goal,
                                                                 int maxLength) {
        List<MyBackLink> backlinks = new ArrayList<>();
        searchAll(new MyBackLink(start, null, 1), goal,
                getNextNodesFunction(),
                backlinks, maxLength);
        List<VertexPath<Integer>> vertexPaths = new ArrayList<>(backlinks.size());
        Deque<Integer> path = new ArrayDeque<>();
        for (MyBackLink list : backlinks) {
            path.clear();
            for (MyBackLink backlink = list; backlink != null; backlink = backlink.parent) {
                path.addFirst(backlink.vertex);
            }
            vertexPaths.add(new VertexPath<Integer>(path));
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
    public @Nullable BackLink search(@NonNull int root,
                                     @NonNull IntPredicate goal,
                                     @NonNull Function<Integer, Spliterator.OfInt> nextNodesFunction,
                                     @NonNull AddToIntSet visited,
                                     int maxLength) {
        Queue<MyBackLink> queue = new ArrayDeque<>(16);
        MyBackLink rootBackLink = new MyBackLink(root, null, maxLength);
        int[] v = new int[1];
        IntConsumer consumer = i -> v[0] = i;
        if (visited.add(root)) {
            queue.add(rootBackLink);
        }

        while (!queue.isEmpty()) {
            MyBackLink node = queue.remove();
            if (goal.test(node.vertex)) {
                return node;
            }

            if (node.maxRemaining > 0) {
                Spliterator.OfInt spliterator = nextNodesFunction.apply(node.vertex);
                while (spliterator.tryAdvance(consumer)) {
                    if (visited.add(v[0])) {
                        MyBackLink backLink = new MyBackLink(v[0], node, node.maxRemaining - 1);
                        queue.add(backLink);
                    }
                }
            }
        }

        return null;
    }

    private void searchAll(@NonNull MyBackLink start, @NonNull IntPredicate goal,
                           @NonNull Function<Integer, Spliterator.OfInt> nextNodesFunction,
                           @NonNull List<MyBackLink> backlinks, int maxDepth) {
        Deque<MyBackLink> stack = new ArrayDeque<>();
        stack.push(start);
        int[] v = new int[1];
        IntConsumer consumer = i -> v[0] = i;

        while (!stack.isEmpty()) {
            MyBackLink current = stack.pop();
            if (goal.test(current.vertex)) {
                backlinks.add(current);
            }
            if (current.maxRemaining < maxDepth) {
                Spliterator.OfInt spliterator = nextNodesFunction.apply(current.vertex);
                while (spliterator.tryAdvance(consumer)) {
                    MyBackLink newPath = new MyBackLink(v[0], current, current.maxRemaining + 1);
                    stack.push(newPath);
                }
            }
        }
    }

    private static class MyBackLink extends BackLink {

        final MyBackLink parent;
        final int vertex;
        final int maxRemaining;

        public MyBackLink(int vertex, MyBackLink parent, int depth) {
            this.vertex = vertex;
            this.parent = parent;
            this.maxRemaining = depth;
        }

        @Override
        BackLink getParent() {
            return parent;
        }

        @Override
        int getVertex() {
            return vertex;
        }
    }

}
