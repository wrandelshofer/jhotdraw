/* @(#)DirectedGraphs.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import static java.lang.Math.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import org.jhotdraw8.collection.ArrayListInt;

/**
 * Provides algorithms for directed graphs.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DirectedGraphs {

    private DirectedGraphs() {
    }

    /**
     * Dumps a directed graph into a String which can be rendered with the "dot" tool.
     * 
     * @param <T> the vertex type
     * @param g the graph
     * @return a "dot" String.
     */
    public static <T> String dump(DirectedGraph<T> g) {
        StringBuilder b = new StringBuilder();

        for (int i = 0, n = g.getVertexCount(); i < n; i++) {
            T v = g.getVertex(i);
            if (g.getNextCount(v) == 0) {
                b.append(v)
                        .append('\n');

            } else {
                for (int j = 0, m = g.getNextCount(v); j < m; j++) {
                    b.append(v)
                            .append(" -> ")
                            .append(g.getNext(v, j))
                            .append('\n');
                }
            }
        }
        return b.toString();
    }

    /**
     * Given a directed graph, returns all disjoint sets of vertices.
     * <p>
     * Uses Kruskal's algorithm.
     *
     * @param <V> the vertex type
     * @param g a directed graph
     * @return the disjoint sets.
     */
    public static <V> List<Set<V>> findDisjointSets(DirectedGraph<V> g) {
        // Create initial forest.
        Map<V, List<V>> sets = new LinkedHashMap<>();
        for (int i = 0, n = g.getVertexCount(); i < n; i++) {
            final V v = g.getVertex(i);
            List<V> initialSet = new ArrayList<>(1);
            initialSet.add(v);
            sets.put(v, initialSet);
        }
        // Merge sets.
        for (int i = 0, n = g.getVertexCount(); i < n; i++) {
            V u = g.getVertex(i);
            for (int j = 0, m = g.getNextCount(u); j < m; j++) {
                V v = g.getNext(u, j);
                List<V> uset = sets.get(u);
                List<V> vset = sets.get(v);
                if (uset != vset) {
                    if (uset.size() < vset.size()) {
                        for (V uu : uset) {
                            sets.put(uu, vset);
                        }
                        vset.addAll(uset);
                    } else {
                        for (V vv : vset) {
                            sets.put(vv, uset);
                        }
                        uset.addAll(vset);
                    }
                }

            }
        }

        // Create final forest.
        Map<List<V>, Object> setMap = new IdentityHashMap<>();
        List<Set<V>> disjointSets = new ArrayList<>();
        for (List<V> set : sets.values()) {
            if (null == setMap.put(set, set)) {
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
     * @param g a directed graph
     * @return the disjoint sets.
     */
    public static List<Set<Integer>> findDisjointSets(IntDirectedGraph g) {
        // Create initial forest.
        final List<ArrayListInt> sets = new ArrayList<>(g.getVertexCount());
        for (int v = 0, n = g.getVertexCount(); v < n; v++) {
            final ArrayListInt initialSet = new ArrayListInt(1);
            initialSet.add(v);
            sets.add(initialSet);
        }
        // Merge sets.
        for (int u = 0, n = g.getVertexCount(); u < n; u++) {
            for (int v = 0, m = g.getNextCount(u); v < m; v++) {
                final ArrayListInt uset = sets.get(u);
                final ArrayListInt vset = sets.get(v);
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
        final Map<ArrayListInt, Object> setMap = new IdentityHashMap<ArrayListInt, Object>();
        final List<Set<Integer>> disjointSets = new ArrayList<>();
        for (ArrayListInt set : sets) {
            if (!setMap.containsKey(set)) {
                setMap.put(set, set);
                disjointSets.add(set.addAllInto(new LinkedHashSet<Integer>()));
            }
        }
        return disjointSets;
    }

    /**
     * Sorts the specified directed graph topologically.
     *
     * @param <V> the vertex type
     * @param m the graph
     * @return the sorted list of vertices
     */
    public static <V> List<V> sortTopologically(DirectedGraph<V> m) {
        final IntDirectedGraph im;
        if (!(m instanceof IntDirectedGraph)) {
            im = DirectedGraphBuilder.ofDirectedGraph(m);
        } else {
            im = (IntDirectedGraph) m;
        }
        int[] a = sortTopologicallyInt(im);
        List<V> result = new ArrayList<>(a.length);
        for (int i = 0; i < a.length; i++) {
            result.add(m.getVertex(a[i]));
        }
        return result;
    }

    /**
     * Sorts the specified directed graph topologically.
     *
     * @param model the graph
     * @return the sorted list of vertices
     */
    public static int[] sortTopologicallyInt(IntDirectedGraph model) {
        final int n = model.getVertexCount();

        // Step 1: compute number of incoming edges for each vertex
        final int[] deg = new int[n]; // number of unprocessed incoming edges on vertex
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
        BitSet doneSet = null;
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
                // Break loop in graph by removing an arbitrary edege.
                if (doneSet == null) {
                    doneSet = new BitSet(n);
                }
                for (int i = doneSet.size(); i < done; i++) {
                    doneSet.set(result[i]);
                }
                for (int i = 0; i < n; i++) {
                    if (!doneSet.get(i)) {
                        deg[i] = 0;
                        queue[last++] = i;
                        break;
                    }
                }
            }
        }

        return result;
    }

    private static class BackLink<VV> {

        final BackLink<VV> parent;
        final VV vertex;

        public BackLink(VV vertex, BackLink<VV> parent) {
            this.vertex = vertex;
            this.parent = parent;
        }

    }

    /**
     * Breadth-first-search.
     *
     * @param <V> the vertex type
     * @param graph a graph
     * @param root the starting point of the search
     * @param goal the goal of the search
     * @param pathElements Adds the resulting path to the provided list of path
     * elements. Does not add the root element.
     * @return true on success
     */
    public static <V> boolean breadthFirstSearch(DirectedGraph<V> graph, V root, V goal, List<V> pathElements) {
        Set<V> visitedSet = new HashSet<>(graph.getVertexCount());// HashSet has a large O(1) cost.
        return breadthFirstSearch(graph, root, goal, pathElements, visitedSet::add);
    }
    /**
     * Breadth-first-search.
     *
     * @param <V> the vertex type
     * @param graph a graph
     * @param root the starting point of the search
     * @param goal the goal of the search
     * @param pathElements Adds the resulting path to the provided list of path
     * elements. Does not add the root element.
     * @param visited a predicate with side effect. The predicate returns true if the specified vertex has been visited, and marks
     * the specified vertex as visited.
     * @return true on success
     */
    public static <V> boolean breadthFirstSearch(DirectedGraph<V> graph, V root, V goal, List<V> pathElements, Predicate<V> visited) {
        Queue<BackLink<V>> queue = new ArrayDeque<>(max(1,min(graph.getVertexCount(),graph.getEdgeCount())));
        BackLink<V> rootBackLink = new BackLink<>(root, null);// temporaly allocated objects producing lots of garbage
        visited.test(root);
        queue.add(rootBackLink);
        BackLink<V> current = null;
        while (!queue.isEmpty()) {
            current = queue.remove();
            if (current.vertex == goal) {
                break;
            }
            for (int i = 0, n = graph.getNextCount(current.vertex); i < n; i++) {
                V next = graph.getNext(current.vertex, i);
                if (visited.test(next)) {
                    BackLink<V> backLink = new BackLink<>(next, current);
                    queue.add(backLink);
                }
            }
        }
        if (current == null || current.vertex != goal) {
            return false;
        }
        for (BackLink<V> i = current; i.vertex != root; i = i.parent) {
            pathElements.add(null);
        }
        int insertionPoint = pathElements.size();
        for (BackLink<V> i = current; i.vertex != root; i = i.parent) {
            pathElements.set(--insertionPoint, i.vertex);
        }
        return true;
    }

    /**
     * Queue with back link store.
     * <p>
     * The back links are stored in the same data structure as the queue and can
     * be retrieved by index even after they have been removed from the queue.
     */
    private static class QueueWithBackLinks {

        final static int Q_NUM_FIELDS = 2;
        final static int Q_PARENT_INDEX = 1;
        final static int Q_VERTEX = 0;
        /**
         * Index at which the next element will be removed from the queue.
         */
        private int first = 0;
        /**
         * Index at which the next element will be added to the queue.
         */
        private int last = 0;
        
        private final int[] queue;

        public QueueWithBackLinks(int capacity) {
            this.queue = new int[capacity];
        }

        /**
         * Adds an element to the end of the queue queue.
         *
         * @param vertex the value of the vertex property of the element
         * @param parentIndex the value of the parent index property of the
         * element
         * @throws IndexOutOfBoundsException if not enough capacity
         */
        public void add(int vertex, int parentIndex) {
            queue[last * Q_NUM_FIELDS + Q_VERTEX] = vertex;
            queue[last * Q_NUM_FIELDS + Q_PARENT_INDEX] = parentIndex;
            ++last;
        }

        /**
         * Gets the index of the last removed element.
         *
         * @return index or SENTINEL if no element has ever been removed
         */
        public int getIndexOfRemoved() {
            return first;
        }

        /**
         * Gets the value of the parent index property of the specified element.
         *
         * @param index the index of an element
         * @return parent index (can be SENTINEL)
         * @throws IndexOutOfBoundsException if index is outside of bounds
         */
        public int getParentIndex(int index) {
            if (index >= last) {
                throw new IndexOutOfBoundsException("index(" + index + ") >= last(" + last + ")");
            }
            return queue[index * Q_NUM_FIELDS + Q_PARENT_INDEX];
        }

        /**
         * Gets the value of the vertex property of the specified element.
         *
         * @param index the index of an element
         * @return vertex
         * @throws IndexOutOfBoundsException if index is outside of bounds
         */
        public int getVertex(int index) {
            if (index >= last) {
                throw new IndexOutOfBoundsException("index(" + index + ") >= last(" + last + ")");
            }
            return queue[index * Q_NUM_FIELDS + Q_VERTEX];
        }

        /**
         * Tests if the queue is empty.
         *
         * @return true if empty.
         */
        public boolean isEmpty() {
            return last == first;
        }

        /**
         * Removes an element from the beginning of the queue.
         *
         * @return the index of the removed back link.
         * @throws NoSuchElementException if the queue is empty
         */
        public int remove() {
            if (isEmpty()) {
                throw new NoSuchElementException();
            }
            int vertex = queue[first * Q_NUM_FIELDS + Q_VERTEX];
            ++first;
            return vertex;
        }
    }

    private final static int SENTINEL = -1;

    /**
     * Breadth-first-search for IntDirectedGraph.
     *
     * @param graph a graph
     * @param root the starting point of the search
     * @param goal the goal of the search
     * @param pathElements Adds the resulting path to the provided list of path
     * elements. Does not add the root element.
     * @return true on success
     */
    public static boolean breadthFirstSearchInt(IntDirectedGraph graph, int root, int goal, ArrayListInt pathElements) {
        BitSet visited = new BitSet(graph.getVertexCount());
        QueueWithBackLinks queue = new QueueWithBackLinks(max(1,min(graph.getVertexCount(),graph.getEdgeCount())));
        queue.add(root, SENTINEL);
        visited.set(root);
        int current = SENTINEL;
        while (!queue.isEmpty()) {
            current = queue.remove();
            if (current == goal) {
                break;
            }
            for (int i = 0, n = graph.getNextCount(current); i < n; i++) {
                int next = graph.getNext(current, i);
                if (!visited.get(next)) {
                    visited.set(next);
                    queue.add(next, current);
                }
            }
        }
        if (current == SENTINEL) {
            return false;
        }
        
        // Add the path to the pathElements list.
        // Part 1. Make room for the path elements.
        int insertionPoint=pathElements.size();
        for (int i = queue.getIndexOfRemoved(); queue.getVertex(i) != root; i = queue.getParentIndex(i)) {
            ++insertionPoint;
        }
        pathElements.setSize(insertionPoint);
        // Part 2. Add the path elements.
        for (int i = queue.getIndexOfRemoved(); queue.getVertex(i) != root; i = queue.getParentIndex(i)) {
            pathElements.set(--insertionPoint, queue.getVertex(i));
        }
        return true;
    }
}
