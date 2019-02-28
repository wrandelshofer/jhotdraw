/* @(#)DirectedGraphs.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.IntArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Provides search algorithms for directed graphs.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntGraphSearch {

    /**
     * Prevents instance creation.
     */
    private IntGraphSearch() {
    }

    @Nonnull
    private static <V, A> Map<V, List<V>> createForest(DirectedGraph<V, A> graph) {
        // Create initial forest.
        Map<V, List<V>> forest = new LinkedHashMap<>(graph.getVertexCount());
        for (V v : graph.getVertices()) {
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
     * Given an int directed graph, returns all disjoint sets of vertices.
     * <p>
     * Uses Kruskal's algorithm.
     *
     * @param <A> the arrow type
     * @param g   a directed graph
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
     * Sorts the specified directed graph topologically.
     *
     * @param <A>   the arrow type
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

}
