/* @(#)DirectedGraphs.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import static java.lang.Math.min;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.Enumerator;
import org.jhotdraw8.collection.IntArrayList;
import org.jhotdraw8.collection.IteratorEnumerator;

/**
 * Provides algorithms for directed graphs.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GraphSearch {

    @Nonnull
    private static <V, A> Map<V, List<V>> createForest(DirectedGraph<V, A> graph) {
        // Create initial forest.
        Map<V, List<V>> forest = new LinkedHashMap<>(graph.getVertexCount());
        for (V v:graph.getVertices()) {
            List<V> initialSet = new ArrayList<>(1);
            initialSet.add(v);
            forest.put(v, initialSet);
        }
        return forest;
    }

    @Nonnull
    private static <V> Map<V, List<V>> createForest(Collection<V> vertices) {
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
     * @param <V> the vertex type
     * @param <A> the arrow type
     * @param graph a directed graph
     * @return the disjoint sets.
     */
    @Nonnull
    public static <V, A> List<Set<V>> findDisjointSets(@Nonnull DirectedGraph<V, A> graph) {
        // Create initial forest
        Map<V, List<V>> forest = createForest(graph);
        // Merge sets.
        for (V u:graph.getVertices()) {
            for (int j = 0, m = graph.getNextCount(u); j < m; j++) {
                V v = graph.getNext(u, j);
                List<V> uset = forest.get(u);
                List<V> vset = forest.get(v);
                if (uset != vset) {
                    union(uset, vset, forest);
                }
            }
        }

        // Create final forest.
        Set<List<V>> visited = new HashSet<>(forest.size());
        List<Set<V>> disjointSets = new ArrayList<>(forest.size());
        for (List<V> set : forest.values()) {
            if (visited.add(set)) {
                disjointSets.add(new LinkedHashSet<>(set));
            }
        }
        return disjointSets;
    }

    /**
     * Given an int directed graph, returns all disjoint sets of vertices.
     * <p>
     * Uses Kruskal's algorithm.
     *
     * @param <A> the arrow type
     * @param g a directed graph
     * @return the disjoint sets.
     */
    @Nonnull
    public static <A> List<Set<Integer>> findDisjointSets(AttributedIntDirectedGraph<?, A> g) {
        // Create initial forest.
        final List<IntArrayList> sets = new ArrayList<>(g.getVertexCount());
        for (int v = 0, n = g.getVertexCount(); v < n; v++) {
            final IntArrayList initialSet = new IntArrayList(1);
            initialSet.add(v);
            sets.add(initialSet);
        }
        // Merge sets.
        for (int u = 0, n = g.getVertexCount(); u < n; u++) {
            for (int v = 0, m = g.getNextCount(u); v < m; v++) {
                final IntArrayList uset = sets.get(u);
                final IntArrayList vset = sets.get(v);
                if (uset != vset) {
                    if (uset.size() < vset.size()) {
                        for (int i = 0, usize = uset.size(); i < usize; i++) {
                            int uu = uset.get(i);
                            sets.set(uu, vset);
                        }
                        vset.addAll(uset);
                    } else {
                        for (int i = 0, vsize = vset.size(); i < vsize; i++) {
                            int vv = vset.get(i);
                            sets.set(vv, uset);
                        }
                        uset.addAll(vset);
                    }
                }
            }
        }
        // Create final forest.
        final Map<IntArrayList, Object> setMap = new HashMap<>();
        final List<Set<Integer>> disjointSets = new ArrayList<>();
        for (IntArrayList set : sets) {
            if (!setMap.containsKey(set)) {
                setMap.put(set, set);
                disjointSets.add(set.addAllInto(new LinkedHashSet<>()));
            }
        }
        return disjointSets;
    }

    /**
     * Given a set of vertices and a list of arrows ordered by cost, returns
     * the minimum spanning tree.
     * <p>
     * Uses Kruskal's algorithm.
     *
     * @param <V> the vertex type
     * @param <A> the arrow type
     * @param vertices a directed graph
     * @param orderedArrows list of arrows sorted by cost in ascending order
     * (lowest cost first, highest cost last).
     * @param rejectedArrows optional, all excluded arrows are added to this
     * list, if it is provided.
     * @return the arrows that are part of the minimum spanning tree.
     */
    @Nonnull
    public static <V, A extends Pair<V>> List<A> findMinimumSpanningTree(@Nonnull Collection<V> vertices, List<A> orderedArrows, @Nullable List<A> rejectedArrows) {
        List<A> minimumSpanningTree = new ArrayList<>(orderedArrows.size());
        if (rejectedArrows == null) {
            rejectedArrows = new ArrayList<>(orderedArrows.size());
        }

        // Create initial forest
        Map<V, List<V>> forest = createForest(vertices);

        // Process arrows from lowest cost to highest cost
        for (A arrow : orderedArrows) {
            List<V> uset = forest.get(arrow.getStart());
            List<V> vset = forest.get(arrow.getEnd());
            if (uset != vset) {
                union(uset, vset, forest);
                minimumSpanningTree.add(arrow);
            } else {
                rejectedArrows.add(arrow);
            }
        }

        return minimumSpanningTree;
    }

    /**
     * Given a set of vertices and a list of arrows ordered by cost, returns a
     * builder with the minimum spanning tree. This is an undirected graph with
     * an arrow in each direction.
     * <p>
     *
     * @param <V> the vertex type
     * @param <A> the arrow type
     * @param vertices the list of vertices
     * @param orderedArrows list of arrows sorted by cost in ascending order
     * (lowest cost first, highest cost last)
     * @param includedArrows optional, all included arrows are added to this
     * list, if it is provided.
     * @param rejectedArrows optional, all excluded arrows are added to this
     * list, if it is provided.
     * @return the graph builder
     */
    @Nonnull
    public static <V, A extends Pair<V>> DirectedGraphBuilder<V, A> findMinimumSpanningTreeGraph(@Nonnull Collection<V> vertices, @Nonnull List<A> orderedArrows, @Nullable List<A> includedArrows, List<A> rejectedArrows) {
        List<A> includedArrowList = findMinimumSpanningTree(vertices, orderedArrows, rejectedArrows);
        if (includedArrows != null) {
            includedArrows.addAll(includedArrowList);
        }
        DirectedGraphBuilder<V, A> builder = new DirectedGraphBuilder<>();
        for (V v : vertices) {
            builder.addVertex(v);
        }
        for (A e : includedArrowList) {
            builder.addArrow(e.getStart(), e.getEnd(), e);
            builder.addArrow(e.getEnd(), e.getStart(), e);
        }
        return builder;
    }

    /**
     * Sorts the specified directed graph topologically.
     *
     * @param <V> the vertex type
     * @param <A> the arrow type
     * @param m the graph
     * @return the sorted list of vertices
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <V, A> List<V> sortTopologically(DirectedGraph<V, A> m) {
        final AttributedIntDirectedGraph<V, A> im;
        if (!(m instanceof AttributedIntDirectedGraph)) {
            im = new DirectedGraphBuilder<>(m);
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
     * @param <A> the arrow type
     * @param model the graph
     * @return the sorted list of vertices
     */
    @Nonnull
    public static <A> int[] sortTopologicallyInt(AttributedIntDirectedGraph<?, A> model) {
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

        // Step 2: put all vertices with degree zero into queue
        final int[] queue = new int[n]; // todo queue
        int first = 0, last = 0; // first and last indices in queue
        for (int i = 0; i < n; i++) {
            if (deg[i] == 0) {
                queue[last++] = i;
            }
        }

        // Step 3: Repeat until all vertices have been processed or a loop has been detected
        final int[] result = new int[n];// result array
        int done = 0;
        Random random = null;
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
                // Break loop in graph by removing an arbitrary arrow.
                if (random == null) {
                    random = new Random(0);
                }
                int i;
                do {
                    i = random.nextInt(n);
                } while (deg[i] <= 0);
                deg[i] = 0;// this can actually remove more than one arrow
                queue[last++] = i;
            }
        }

        return result;
    }

    private static <V> void union(@Nonnull List<V> uset, @Nonnull List<V> vset, @Nonnull Map<V, List<V>> forest) {
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
     * Returns all stronlgy connected components in the specified graph.
     *
     * @param <V>
     * @param maxIterations
     * @param vertices
     * @param nextNodeFunction
     * @return set of strongly connected components (sets of vertices).
     */
    public static <V> List<List<V>> searchStronglyConnectedComponents(
            final int maxIterations,
            final Collection<? extends V> vertices,
            final Function<V, Iterable<? extends V>> nextNodeFunction
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

        int count = 0;
        STRONGCONNECT:
        while (true) {
            if (count++ > maxIterations) {
                throw new IllegalArgumentException("too many iterations");
            }

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
