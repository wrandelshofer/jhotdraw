package org.jhotdraw8.graph;

import org.jhotdraw8.annotation.Nonnull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Builder for creating unique paths from a directed graph.
 * <p>
 * The builder searches for unique paths using a breadth-first search.<br>
 * Returns only a path if it is unique.
 *
 * @param <V> the vertex type
 * @param <A> the arrow type
 * @author Werner Randelshofer
 */
public class UniquePathBuilder<V, A> extends AbstractPathBuilder<V, A> {

    public UniquePathBuilder(@Nonnull DirectedGraph<V, A> g) {
        this(g::getNextVertices);
    }

    public UniquePathBuilder(@Nonnull Function<V, Iterable<V>> nextNodesFunction) {
        super(nextNodesFunction);
    }

    private static class MyBackLink<VV, AA> extends BackLink<VV, AA> {

        final MyBackLink<VV, AA> parent;
        final VV vertex;
        final int depth;

        public MyBackLink(VV vertex, MyBackLink<VV, AA> parent, int depth) {
            this.vertex = vertex;
            this.parent = parent;
            this.depth = depth;
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


    protected BackLink<V, A> search(@Nonnull V root,
                                    @Nonnull Predicate<V> goal,
                                    Function<V, Iterable<V>> nextNodesFunction,
                                    @Nonnull Predicate<V> visited,
                                    int maxLength) {

        Deque<MyBackLink<V, A>> queue = new ArrayDeque<>(16);

        MyBackLink<V, A> rootBackLink = new MyBackLink<>(root, null, maxLength);
        visited.test(root);
        queue.add(rootBackLink);
        MyBackLink<V, A> current = null;
        MyBackLink<V, A> found = null;
        Set<V> nonUnique = new LinkedHashSet<>();
        while (!queue.isEmpty()) {
            current = queue.removeFirst();
            if (goal.test(current.vertex)) {
                if (found != null) {
                    // path is not unique!
                    return null;
                }
                found = current;
            }
            if (current.depth > 0) {
                for (V next : nextNodesFunction.apply(current.vertex)) {
                    if (visited.test(next)) {
                        MyBackLink<V, A> backLink = new MyBackLink<V, A>(next, current, current.depth - 1);
                        queue.addLast(backLink);
                    } else {
                        nonUnique.add(next);
                    }
                }
            }
        }
        queue.clear();
        if (found == null) {
            return null;
        }
        current = found;
        while (current != null) {
            if (nonUnique.contains(current.vertex)) {
                // path is not unique!
                return null;
            }
            current = current.parent;
        }
        return found;
    }

}
