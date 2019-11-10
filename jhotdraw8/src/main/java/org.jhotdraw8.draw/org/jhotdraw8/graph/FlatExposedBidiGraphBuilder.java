/*
 * @(#)FlatExposedBidiGraphBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Enumerator;
import org.jhotdraw8.collection.SpliteratorIterable;

import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Provides an API for building a {@code BidiGraph}.
 * <p>
 * Performance:
 * <p>
 * This builder exposes its vertex and arrow data objects.
 * This can improve performance, because this allows to save one memory access per vertex and per arrow.
 * Also, there is no need for looking up data objects in a hash map.
 * <p>
 * A Vertex or an Arrow may only be added to one graph at a time.
 * For performance reasons, this class does not check if a Vertex or
 * an Arrow has been accidentally added to multiple graphs.
 * <p>
 * The data objects are flattened for performance.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 */
public class FlatExposedBidiGraphBuilder<V extends FlatExposedBidiGraphBuilder.Vertex<V, A>, A extends FlatExposedBidiGraphBuilder.Arrow<V, A>> implements BidiGraph<V, A> {
    private int arrowCount;
    @NonNull
    private final Set<V> vertices;

    /**
     * Creates a new instance with default capacity for vertices and arrows.
     */
    public FlatExposedBidiGraphBuilder() {
        this(10, 10);
    }

    /**
     * Creates a new instance with the specified capacities.
     *
     * @param vertexCapacity the vertex capacity
     * @param arrowCapacity  the arrow capaicty
     */
    public FlatExposedBidiGraphBuilder(int vertexCapacity, int arrowCapacity) {

        vertices = new LinkedHashSet<>(vertexCapacity);
    }

    /**
     * Creates a new instance which is a clone of the specified graph.
     *
     * @param that another graph
     */
    public FlatExposedBidiGraphBuilder(@NonNull DirectedGraph<V, A> that) {
        this(that, Function.identity(), Function.identity());
    }

    /**
     * Creates a new instance which is a clone of the specified graph using the
     * provided mapping functions.
     *
     * @param <VV>         the vertex type of that
     * @param <AA>         the arrow type of that
     * @param that         another graph
     * @param vertexMapper a mapping function from that vertex type to the this
     *                     vertex type
     * @param arrowMapper  a mapping function from that arrow type to the this
     *                     arrow type
     */
    public <VV, AA> FlatExposedBidiGraphBuilder(@NonNull DirectedGraph<VV, AA> that, @NonNull Function<VV, V> vertexMapper, @NonNull Function<AA, A> arrowMapper) {
        vertices = new LinkedHashSet<>(that.getVertexCount());

        for (VV vv : that.getVertices()) {
            addVertex(vertexMapper.apply(vv));
        }
        for (VV vv : that.getVertices()) {
            V v = vertexMapper.apply(vv);
            for (int i = 0, n = that.getNextCount(vv); i < n; i++) {
                addArrow(v, vertexMapper.apply(that.getNext(vv, i)), arrowMapper.apply(that.getNextArrow(vv, i)));
            }
        }
    }

    /**
     * Adds the specified arrow from vertex 'from' to vertex 'to'.
     *
     * @param start the vertex
     * @param end   the vertex
     * @param arrow the arrow, can be null
     */
    public void addArrow(@NonNull V start, @NonNull V end, @Nullable A arrow) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start=" + start + ", end=" + end + ", arrow=" + arrow);
        }
        if (arrow.getStart() != start || arrow.getEnd() != end) {
            throw new IllegalArgumentException("start=" + start + ", end=" + end + ", arrow=" + arrow);
        }
        start.addNext(arrow);
        end.addPrev(arrow);
    }


    /**
     * Adds the specified vertex to the graph.
     *
     * @param v the vertex
     */
    public void addVertex(V v) {
        vertices.add(v);
    }

    public void clear() {
        vertices.clear();
        arrowCount = 0;
    }

    @Override
    public int getArrowCount() {
        return arrowCount;
    }

    @NonNull
    @Override
    public V getNext(@NonNull V vertex, int i) {
        return getVertexDataNonNull(vertex).getNext(i);
    }

    @NonNull
    @Override
    public A getNextArrow(@NonNull V vertex, int index) {
        return getVertexDataNonNull(vertex).getNextArrow(index);
    }

    @Override
    public int getNextCount(@NonNull V vertex) {
        return getVertexDataNonNull(vertex).getNextCount();
    }

    @NonNull
    @Override
    public V getPrev(@NonNull V vertex, int i) {
        return getVertexDataNonNull(vertex).getPrev(i);
    }

    @NonNull
    @Override
    public A getPrevArrow(@NonNull V vertex, int index) {
        return getVertexDataNonNull(vertex).getPrevArrow(index);
    }

    @Override
    public int getPrevCount(@NonNull V vertex) {
        return getVertexDataNonNull(vertex).getPrevCount();
    }

    @Override
    public int getVertexCount() {
        return vertices.size();
    }

    @NonNull
    @Override
    public Collection<V> getVertices() {
        return Collections.unmodifiableCollection(vertices);
    }


    private V getVertexDataNonNull(V vertex) {
        return vertex;
    }

    /**
     * Removes the specified "next" arrow.
     *
     * @param v a vertex
     * @param a an arrow starting at the vertex, must not be null
     */
    @SuppressWarnings("unused")
    public void removeArrow(@NonNull V v, @NonNull A a) {
        for (int i = 0, n = getNextCount(v); i < n; i++) {
            if (a.equals(getNextArrow(v, i))) {
                removeNext(v, i);
                break;
            }
        }
    }

    /**
     * Removes the specified arrow from the graph.
     *
     * @param start the start vertex of the arrow
     * @param i     the index of the i-th next vertex
     */
    @SuppressWarnings("WeakerAccess")
    public void removeNext(@NonNull V start, int i) {
        A a = start.getNextArrow(i);
        final V endData = a.end;
        start.removeNext(i);
        endData.removePrev(a);
        arrowCount--;
    }

    /**
     * Removes the specified arrow from the graph.
     *
     * @param start the start vertex of the arrow
     * @param end   the end vertex of the arrow
     */
    @SuppressWarnings("unused")
    public void removeNext(@NonNull V start, V end) {
        for (int i = 0, n = getNextCount(start); i < n; i++) {
            if (getNext(start, i).equals(end)) {
                removeNext(start, i);
                return;
            }
        }
    }

    /**
     * Removes the specified vertex from the graph and all arrows that start or
     * end at the vertex.
     *
     * @param v the vertex
     */
    @SuppressWarnings("unused")
    public void removeVertex(@Nullable V v) {
        if (v == null) {
            return;
        }
        for (int i = v.getNextCount() - 1; i >= 0; i--) {
            removeNext(v, i);
        }
        for (int i = v.getPrevCount() - 1; i >= 0; i--) {
            A arrow = v.getPrevArrow(i);
            removeNext(arrow.start, (arrow.start).indexOfNext(arrow));
        }
        vertices.remove(v);
    }

    /**
     * Represents an arrow data object.
     *
     * @param <V> the vertex data object type
     * @param <A> the arrow data object type
     */
    public static class Arrow<V extends Vertex<V, A>, A extends Arrow<V, A>> {

        @NonNull
        final V start;
        @NonNull
        final V end;

        public Arrow(@NonNull final V start, @NonNull final V end) {
            this.start = start;
            this.end = end;
        }

        @NonNull
        public V getStart() {
            return start;
        }

        @NonNull
        public V getEnd() {
            return end;
        }
    }

    private static class PrevNextIterator<V> implements Iterator<V> {
        int index = 0;
        final int count;
        final Object[] next;

        PrevNextIterator(int count, Object[] next) {
            this.count = count;
            this.next = next;
        }

        @Override
        public boolean hasNext() {
            return index < count;
        }

        @NonNull
        @SuppressWarnings("unchecked")
        @Override
        public V next() {
            return (V) next[(index++) * 2];
        }
    }

    /**
     * Represents a vertex data object.
     *
     * @param <V> the vertex data object type
     * @param <A> the arrow data object type
     */
    public static class Vertex<V extends Vertex<V, A>, A extends Arrow<V, A>> {
        private int nextCount;
        private int prevCount;
        /**
         * Even entries contain vertices, odd entries contain arrows.
         */
        private Object[] next;
        /**
         * Even entries contain vertices, odd entries contain arrows.
         */
        private Object[] prev;

        public Vertex() {
        }

        private final static int INITIAL_SIZE = 10;

        @NonNull
        @SuppressWarnings("unchecked")
        V getNext(int idx) {
            return (V) next[idx * 2];
        }

        @NonNull
        @SuppressWarnings("unchecked")
        A getNextArrow(int idx) {
            return (A) next[idx * 2 + 1];
        }

        int getNextCount() {
            return nextCount;
        }

        void addNext(@NonNull A arrow) {
            if (next == null) {
                next = new Object[INITIAL_SIZE];
            } else if (next.length <= nextCount * 2) {
                Object[] tmp = next;
                next = new Object[next.length * 2];
                System.arraycopy(tmp, 0, next, 0, tmp.length);
            }
            next[nextCount * 2] = arrow.end;
            next[nextCount * 2 + 1] = arrow;
            nextCount++;
        }

        @NonNull Iterator<V> getPrevIterator() {
            return new PrevNextIterator<>(prevCount, prev);
        }

        @NonNull Iterator<V> getNextIterator() {
            return new PrevNextIterator<>(nextCount, next);
        }

        @NonNull
        @SuppressWarnings("unchecked")
        V getPrev(int idx) {
            return (V) prev[idx * 2];
        }

        @NonNull
        @SuppressWarnings("unchecked")
        A getPrevArrow(int idx) {
            return (A) prev[idx * 2 + 1];
        }

        int getPrevCount() {
            return prevCount;
        }

        void addPrev(@NonNull A arrow) {
            if (prev == null) {
                prev = new Object[INITIAL_SIZE];
            } else if (prev.length <= prevCount * 2) {
                Object[] tmp = prev;
                prev = new Object[prev.length * 2];
                System.arraycopy(tmp, 0, prev, 0, tmp.length);
            }
            prev[prevCount * 2] = arrow.start;
            prev[prevCount * 2 + 1] = arrow;
            prevCount++;
        }

        int indexOfNext(A arrow) {
            for (int i = 0; i < nextCount; i++) {
                if (next[i * 2] == arrow) {
                    return i;
                }
            }
            return -1;
        }

        void removeNext(A arrow) {
            removeNext(indexOfNext(arrow));
        }

        void removeNext(int idx) {
            System.arraycopy(next, (idx + 1) * 2, next, idx * 2, (nextCount - idx - 1) * 2);
            nextCount--;
        }

        int indexOfPrev(A arrow) {
            for (int i = 0; i < prevCount; i++) {
                if (prev[i * 2] == arrow) {
                    return i;
                }
            }
            return -1;
        }

        void removePrev(A arrow) {
            removePrev(indexOfPrev(arrow));
        }

        void removePrev(int idx) {
            System.arraycopy(prev, (idx + 1) * 2, prev, idx * 2, (prevCount - idx - 1) * 2);
            prevCount--;
        }
    }

    @NonNull
    @Override
    public Iterable<V> breadthFirstSearchBackward(final V start, @NonNull final Predicate<V> visited) {
        return new SpliteratorIterable<>(() -> new BidiBreadthFirstSpliteratorBackward<>(getVertexDataNonNull(start), visited));
    }

    @NonNull
    @Override
    public Iterable<V> breadthFirstSearch(final V start, @NonNull final Predicate<V> visited) {
        return new SpliteratorIterable<>(() -> new BidiBreadthFirstSpliteratorForward<>(getVertexDataNonNull(start), visited));
    }

    /**
     * This is a performance-optimized implementation which does not need to call a hash function for
     * every vertex.
     */
    private static abstract class BidiBreadthFirstSpliterator<V> extends Spliterators.AbstractSpliterator<V> implements Enumerator<V>, Consumer<V> {

        @NonNull
        private final Queue<V> queue;
        @NonNull
        private final Predicate<V> visited;

        private V current;

        /**
         * Creates a new instance.
         *
         * @param root    the root vertex
         * @param visited a predicate with side effect. The predicate returns true
         *                if the specified vertex has been visited, and marks the specified vertex
         *                as visited.
         */
        BidiBreadthFirstSpliterator(
                @NonNull final V root, @NonNull final Predicate<V> visited) {
            super(Long.MAX_VALUE, ORDERED | DISTINCT | NONNULL);
            Objects.requireNonNull(root, "root");
            Objects.requireNonNull(visited, "vistied");
            queue = new ArrayDeque<>(16);
            this.visited = visited;
            queue.add(root);
            visited.test(root);
        }

        @Override
        public boolean moveNext() {
            return tryAdvance(this);
        }

        @Override
        public V current() {
            return current;
        }

        @Override
        public boolean tryAdvance(@NonNull final Consumer<? super V> action) {
            final V current = queue.poll();
            if (current == null) {
                return false;
            }
            for (final Iterator<V> i = getNextNodes(current); i.hasNext(); ) {
                V endData = i.next();
                if (visited.test(endData)) {
                    queue.add(endData);
                }
            }
            action.accept(current);
            return true;
        }

        @Override
        public void accept(V o) {
            current = o;
        }

        @NonNull
        abstract Iterator<V> getNextNodes(V v);
    }

    static class BidiBreadthFirstSpliteratorForward<V extends Vertex<V, A>, A extends Arrow<V, A>> extends BidiBreadthFirstSpliterator<V> {

        public BidiBreadthFirstSpliteratorForward(@NonNull V root, @NonNull Predicate<V> visited) {
            super(root, visited);
        }

        @NonNull
        @Override
        Iterator<V> getNextNodes(@NonNull V v) {
            return v.getNextIterator();
        }

    }

    static class BidiBreadthFirstSpliteratorBackward<V extends Vertex<V, A>, A extends Arrow<V, A>> extends BidiBreadthFirstSpliterator<V> {

        public BidiBreadthFirstSpliteratorBackward(@NonNull V root, @NonNull Predicate<V> visited) {
            super(root, visited);
        }

        @NonNull
        @Override
        Iterator<V> getNextNodes(@NonNull V v) {
            return v.getPrevIterator();
        }
    }

    @NonNull
    @Override
    public Collection<A> getArrows() {
        class ArrowIterator implements Iterator<A> {
            @NonNull
            private final Iterator<V> vertexIterator;
            private Iterator<A> nextArrowIterator;

            ArrowIterator() {
                arrowCount = getArrowCount();
                vertexIterator = getVertices().iterator();
                nextArrowIterator = Collections.emptyIterator();
            }

            @Override
            public boolean hasNext() {
                return nextArrowIterator.hasNext() || vertexIterator.hasNext();
            }

            @Override
            @Nullable
            public A next() {
                while (!nextArrowIterator.hasNext()) {
                    V v = vertexIterator.next();
                    nextArrowIterator = getNextArrows(v).iterator();
                }
                return nextArrowIterator.next();
            }

        }
        return new AbstractCollection<A>() {
            @NonNull
            @Override
            public Iterator<A> iterator() {
                return new ArrowIterator();
            }

            @Override
            public int size() {
                return getArrowCount();
            }

        };
    }
}
