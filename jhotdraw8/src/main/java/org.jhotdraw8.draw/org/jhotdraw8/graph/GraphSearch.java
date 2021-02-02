/*
 * @(#)GraphSearch.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Enumerator;
import org.jhotdraw8.collection.IteratorEnumerator;
import org.jhotdraw8.collection.Pair;
import org.jhotdraw8.collection.UnorderedPair;
import org.jhotdraw8.util.ToDoubleTriFunction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import static java.lang.Math.min;

/**
 * Provides search algorithms for directed graphs.
 *
 * @author Werner Randelshofer
 */
public class GraphSearch {

    @NonNull
    private static <V> Map<V, List<V>> createForest(@NonNull Collection<V> vertices) {
        // Create initial forest.
        Map<V, List<V>> forest = new LinkedHashMap<>(vertices.size());
        for (V v : vertices) {
            List<V> initialSet = new ArrayList<>(1);
            initialSet.add(v);
            forest.put(v, initialSet);
        }
        return forest;
    }

    /**
     * Given a directed graph, returns all disjoint sets of vertices.
     * <p>
     * Uses Kruskal's algorithm.
     *
     * @param <V>   the vertex type
     * @param <A>   the arrow type
     * @param graph a directed graph
     * @return the disjoint sets.
     */
    @NonNull
    public static <V, A> List<Set<V>> findDisjointSets(@NonNull DirectedGraph<V, A> graph) {
        return findDisjointSets(graph.getVertices(), graph::getNextVertices);
    }

    /**
     * Given a directed graph, returns all disjoint sets of vertices.
     * <p>
     * Uses Kruskal's algorithm.
     *
     * @param <V>             the vertex type
     * @param vertices        the vertices of the directed graph
     * @param getNextVertices a function that returns the next vertices given a vertex
     * @return the disjoint sets.
     */
    @NonNull
    public static <V> List<Set<V>> findDisjointSets(@NonNull Collection<V> vertices, @NonNull Function<V, Iterable<V>> getNextVertices) {
        // Create initial forest
        Map<V, List<V>> forest = createForest(vertices);
        // Merge sets.
        for (V u : vertices) {
            for (V v : getNextVertices.apply(u)) {
                List<V> uset = forest.get(u);
                List<V> vset = forest.get(v);
                if (uset != vset) {
                    union(uset, vset, forest);
                }
            }
        }

        // Create final forest.
        Set<List<V>> visited = Collections.newSetFromMap(new IdentityHashMap<>(forest.size()));
        List<Set<V>> disjointSets = new ArrayList<>(forest.size());
        for (List<V> set : forest.values()) {
            if (visited.add(set)) {
                disjointSets.add(new LinkedHashSet<>(set));
            }
        }
        return disjointSets;
    }

    private static class Edge<VV, AA> extends UnorderedPair<VV> {
        private final AA arrow;
        private final double cost;

        public Edge(VV a, VV b, AA arrow, double cost) {
            super(a, b);
            this.arrow = arrow;
            this.cost = cost;
        }
    }

    /**
     * Given an undirected graph and a cost function, returns a builder
     * with the minimum spanning tree.
     * <p>
     *
     * @param <V>   the vertex type
     * @param <A>   the arrow type
     * @param graph the graph. This must be an undirected graph
     *              represented as a directed graph with two identical arrows for each edge.
     * @param costf the cost function
     * @return the graph builder
     */
    @NonNull
    public static <V, A> DirectedGraphBuilder<V, A> findMinimumSpanningTreeGraph(@NonNull DirectedGraph<V, A> graph, @NonNull ToDoubleFunction<A> costf) {
        return findMinimumSpanningTreeGraph(graph, (u, v, a) -> costf.applyAsDouble(a));

    }

    /**
     * Given an undirected graph and a cost function, returns a builder
     * with the minimum spanning tree.
     * <p>
     *
     * @param <V>   the vertex type
     * @param <A>   the arrow type
     * @param graph the graph. This must be an undirected graph
     *              represented as a directed graph with two identical arrows for each edge.
     * @param costf the cost function
     * @return the graph builder
     */
    @NonNull
    public static <V, A> DirectedGraphBuilder<V, A> findMinimumSpanningTreeGraph(@NonNull DirectedGraph<V, A> graph, @NonNull ToDoubleTriFunction<V, V, A> costf) {
        Collection<V> vertices = graph.getVertices();
        Set<V> done = new HashSet<>();
        List<Edge<V, A>> edges = new ArrayList<>();
        for (V start : vertices) {
            done.add(start);
            for (Arc<V, A> entry : graph.getNextArcs(start)) {
                V end = entry.getEnd();
                A arrow = entry.getData();
                if (!done.contains(end)) {
                    edges.add(new Edge<>(start, end, arrow, costf.applyAsDouble(start, end, arrow)));
                }
            }
        }
        edges.sort(Comparator.comparingDouble(a -> a.cost));
        List<Edge<V, A>> mst = findMinimumSpanningTree(vertices, edges, null);
        DirectedGraphBuilder<V, A> builder = new DirectedGraphBuilder<>(vertices.size(), mst.size() * 2);
        for (V v : vertices) {
            builder.addVertex(v);
        }
        for (Edge<V, A> e : mst) {
            builder.addArrow(e.first(), e.second(), e.arrow);
            builder.addArrow(e.second(), e.first(), e.arrow);
        }
        return builder;
    }

    /**
     * Given a set of vertices and a list of arrows ordered by cost, returns a
     * builder with the minimum spanning tree. This is an undirected graph with
     * an arrow in each direction.
     * <p>
     *
     * @param <V>            the vertex type
     * @param <A>            the arrow type
     * @param vertices       the list of vertices
     * @param orderedArrows  list of arrows sorted by cost in ascending order
     *                       (lowest cost first, highest cost last)
     * @param includedArrows optional, all included arrows are added to this
     *                       list, if it is provided.
     * @param rejectedArrows optional, all excluded arrows are added to this
     *                       list, if it is provided.
     * @return the graph builder
     */
    @NonNull
    public static <V, A extends Pair<V, V>> DirectedGraphBuilder<V, A> findMinimumSpanningTreeGraph(@NonNull Collection<V> vertices, @NonNull List<A> orderedArrows, @Nullable List<A> includedArrows, List<A> rejectedArrows) {
        List<A> includedArrowList = findMinimumSpanningTree(vertices, orderedArrows, rejectedArrows);
        if (includedArrows != null) {
            includedArrows.addAll(includedArrowList);
        }
        DirectedGraphBuilder<V, A> builder = new DirectedGraphBuilder<>();
        for (V v : vertices) {
            builder.addVertex(v);
        }
        for (A e : includedArrowList) {
            builder.addArrow(e.first(), e.second(), e);
            builder.addArrow(e.second(), e.first(), e);
        }
        return builder;
    }

    /**
     * Given a set of vertices and a list of arrows ordered by cost, returns
     * the minimum spanning tree.
     * <p>
     * Uses Kruskal's algorithm.
     *
     * @param <V>           the vertex type
     * @param <P>           the arrow type
     * @param vertices      a directed graph
     * @param orderedEdges  list of edges sorted by cost in ascending order
     *                      (lowest cost first, highest cost last).
     * @param rejectedEdges optional, all excluded edges are added to this
     *                      list, if it is provided.
     * @return the arrows that are part of the minimum spanning tree.
     */
    @NonNull
    public static <V, P extends Pair<V, V>> List<P> findMinimumSpanningTree(@NonNull Collection<V> vertices, @NonNull List<P> orderedEdges, @Nullable List<P> rejectedEdges) {
        List<P> minimumSpanningTree = new ArrayList<>(orderedEdges.size());
        if (rejectedEdges == null) {
            rejectedEdges = new ArrayList<>(orderedEdges.size());
        }

        // Create initial forest
        Map<V, List<V>> forest = createForest(vertices);

        // Process arrows from lowest cost to highest cost
        for (P arrow : orderedEdges) {
            List<V> uset = forest.get(arrow.first());
            List<V> vset = forest.get(arrow.second());
            if (uset != vset) {
                union(uset, vset, forest);
                minimumSpanningTree.add(arrow);
            } else {
                rejectedEdges.add(arrow);
            }
        }

        return minimumSpanningTree;
    }

    /**
     * Sorts the specified directed graph topologically.
     *
     * @param <V> the vertex type
     * @param <A> the arrow type
     * @param m   the graph
     * @return the sorted list of vertices
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public static <V, A> List<V> sortTopologically(DirectedGraph<V, A> m) {
        final AttributedIntDirectedGraph<V, A> im;
        if (!(m instanceof AttributedIntDirectedGraph)) {
            return sortTopologicallyObject(m);
        } else {
            im = (AttributedIntDirectedGraph<V, A>) m;
        }
        int[] a = sortTopologicallyInt(im);
        List<V> result = new ArrayList<>(a.length);
        for (int i = 0; i < a.length; i++) {
            result.add(im.getVertex(a[i]));
        }
        return result;
    }


    /**
     * Sorts the specified directed graph topologically.
     *
     * @param model the graph
     * @return the sorted list of vertices
     */
    @NonNull
    public static int[] sortTopologicallyInt(@NonNull IntDirectedGraph model) {
        final int n = model.getVertexCount();

        // Step 1: compute number of incoming arrows for each vertex
        final int[] deg = new int[n]; // deg is the number of unprocessed incoming arrows on vertex
        for (int i = 0; i < n; i++) {
            final int m = model.getNextCount(i);
            for (int j = 0; j < m; j++) {
                int v = model.getNext(i, j);
                deg[v]++;
            }
        }

        // Step 2: put all vertices with degree zero into deque
        final int[] queue = new int[n]; // todo deque
        int first = 0, last = 0; // first and last indices in deque
        for (int i = 0; i < n; i++) {
            if (deg[i] == 0) {
                queue[last++] = i;
            }
        }

        // Step 3: Repeat until all vertices have been processed or a loop has been detected
        final int[] result = new int[n];// result array
        int done = 0;
        while (done < n) {
            for (; done < n; done++) {
                if (first == last) {
                    // => the graph has a loop!
                    break;
                }
                int v = queue[first++];
                final int m = model.getNextCount(v);
                for (int j = 0; j < m; j++) {
                    int u = model.getNext(v, j);
                    if (--deg[u] == 0) {
                        queue[last++] = u;
                    }
                }
                result[done] = v;
            }

            if (done < n) {
                // Break loop in graph by removing all arrows on a node.
                int i = 0;
                while (i < n - 1 && deg[i] <= 0) {
                    i++;
                }
                ;
                if (deg[i] == 0) {
                    throw new AssertionError("bug in loop-breaking algorithm i: " + i);
                }
                deg[i] = 0;// this can actually remove more than one arrow
                queue[last++] = i;
            }
        }

        return result;
    }

    /**
     * Sorts the specified directed graph topologically.
     *
     * @param <V>   the vertex type
     * @param <A>   the arrow type
     * @param model the graph
     * @return the sorted list of vertices
     */
    @NonNull
    public static <V, A> List<V> sortTopologicallyObject(DirectedGraph<V, A> model) {
        return sortTopologically(model.getVertices(), model::getNextVertices);
    }

    /**
     * Sorts the specified directed graph topologically.
     * <p>
     * If the graph contains cycles, then this method splits the graph
     * inside of a cycle.
     *
     * @param <V>          the vertex type
     * @param vertices     the vertices of the graph
     * @param nextVertices a function that delivers the next vertices for a given vertex
     * @return the sorted list of vertices
     */
    @NonNull
    public static <V> List<V> sortTopologically(@NonNull Collection<V> vertices,
                                                @NonNull Function<V, Iterable<? extends V>> nextVertices) {
        final int n = vertices.size();
        Set<V> verticesInLoops = null;

        // Step 1: compute number of incoming arrows for each vertex
        final Map<V, Integer> deg = new LinkedHashMap<>(n); // deg is the number of unprocessed incoming arrows on vertex
        for (V v : vertices) {
            deg.putIfAbsent(v, 0);
            for (V u : nextVertices.apply(v)) {
                deg.merge(u, 1, Integer::sum);
            }
        }

        // Step 2: put all vertices with degree zero into queue
        final Queue<V> queue = new ArrayDeque<>(n);
        for (Map.Entry<V, Integer> entry : deg.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        // Step 3: Repeat until all vertices have been processed or a loop has been detected
        final List<V> result = new ArrayList<>(n);// result array
        int done = 0;
        while (done < n) {
            for (; done < n; done++) {
                if (queue.isEmpty()) {
                    // => the graph has a loop!
                    break;
                }
                V v = queue.remove();
                for (V u : nextVertices.apply(v)) {
                    if (deg.merge(u, -1, Integer::sum) == 0) {
                        queue.add(u);
                    }
                }
                result.add(v);
            }

            if (done < n) {
                // FIXME only search in remaining subgraph
                if (verticesInLoops == null) {
                    List<List<V>> stronglyConnectedComponents = findStronglyConnectedComponents(vertices, nextVertices);
                    verticesInLoops = new LinkedHashSet<>();
                    for (List<V> stronglyConnectedComponent : stronglyConnectedComponents) {
                        if (stronglyConnectedComponent.size() > 1) {
                            verticesInLoops.addAll(stronglyConnectedComponent);
                        }
                    }
                }

                // Break loop in graph by removing an arbitrary arrow.
                boolean didBreakLoop = false;
                for (V v : verticesInLoops) {
                    if (deg.get(v) > 0) {
                        deg.put(v, 0);// this can actually remove more than one arrow
                        queue.add(v);
                        didBreakLoop = true;
                        break;
                    }
                }
                if (!didBreakLoop) {
                    throw new AssertionError("Programming error in loop breaking code.");
                }
            }
        }

        return result;
    }


    private static <V> void union(@NonNull List<V> uset, @NonNull List<V> vset, @NonNull Map<V, List<V>> forest) {
        if (uset != vset) {
            if (uset.size() < vset.size()) {
                for (V uu : uset) {
                    forest.put(uu, vset);
                }
                vset.addAll(uset);
            } else {
                for (V vv : vset) {
                    forest.put(vv, uset);
                }
                uset.addAll(vset);
            }
        }
    }

    /**
     * Holds bookkeeping data for a node v from the graph.
     */
    private static class NodeData {

        /**
         * Low represents the smallest index of any node known to be reachable from v through v's DFS subtree,
         * including v itself.
         * <p>
         * Therefore v must be left on the stack if v.low < v.index, whereas v must be removed as the root of a
         * strongly connected component if v.low == v.index.
         * <p>
         * The value v.low is computed during the depth-first search from v, as this finds the nodes that are reachable from v.
         */
        private int low;

    }


    /**
     * Returns all strongly connected components in the specified graph.
     *
     * @param graph the graph
     * @param <V>   the vertex type
     * @param <A>   the arrow type
     * @return set of strongly connected components (sets of vertices).
     */
    @NonNull
    public static <V, A> List<List<V>> findStronglyConnectedComponents(
            @NonNull final DirectedGraph<V, A> graph) {
        return findStronglyConnectedComponents(graph.getVertices(), graph::getNextVertices);
    }

    /**
     * Returns all strongly connected components in the specified graph.
     *
     * @param <V>              the vertex type
     * @param vertices         the vertices of the graph
     * @param nextNodeFunction returns the next nodes of a given node
     * @return set of strongly connected components (sets of vertices).
     */
    @NonNull
    public static <V> List<List<V>> findStronglyConnectedComponents(
            @NonNull final Collection<? extends V> vertices, @NonNull final Function<V, Iterable<? extends V>> nextNodeFunction
    ) {
        // The following non-recursive implementation "Tarjan's strongly connected components"
        // algorithm has been taken from
        // https://stackoverflow.com/questions/46511682/non-recursive-version-of-tarjans-algorithm

        final List<List<V>> sccs = new ArrayList<>();
        final Map<V, NodeData> nodeMap = new HashMap<>();

        int pre = 0;
        Deque<V> stack = new ArrayDeque<>();

        Deque<Integer> minStack = new ArrayDeque<>();
        Deque<Enumerator<V>> enumeratorStack = new ArrayDeque<>();
        Enumerator<V> enumerator = new IteratorEnumerator<>(vertices.iterator());

        STRONGCONNECT:
        while (true) {
            if (enumerator.moveNext()) {
                V v = enumerator.current();
                NodeData vdata = nodeMap.get(v);
                if (vdata == null) {
                    vdata = new NodeData();
                    nodeMap.put(v, vdata);
                    vdata.low = pre++;
                    stack.push(v);
                    // Level down:
                    minStack.push(vdata.low);
                    enumeratorStack.push(enumerator);
                    enumerator = new IteratorEnumerator<>(nextNodeFunction.apply(v).iterator());
                } else {
                    if (!minStack.isEmpty()) {
                        minStack.push(min(vdata.low, minStack.pop()));
                    }
                }
            } else {
                // Level up:
                if (enumeratorStack.isEmpty()) {
                    break STRONGCONNECT;
                }

                enumerator = enumeratorStack.pop();
                V v = enumerator.current();
                int min = minStack.pop();
                NodeData vdata = nodeMap.get(v);
                if (min < vdata.low) {
                    vdata.low = min;
                } else {
                    List<V> component = new ArrayList<>();
                    V w;
                    do {
                        w = stack.pop();
                        component.add(w);
                        NodeData wdata = nodeMap.get(w);
                        wdata.low = vertices.size();
                    } while (!w.equals(v));
                    sccs.add(component);
                }

                if (!minStack.isEmpty()) {
                    minStack.push(min(vdata.low, minStack.pop()));
                }
            }
        }
        return sccs;
    }

    /**
     * Prevents instance creation.
     */
    private GraphSearch() {
    }

}
