/*
 * @(#)IterableTree.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.collection;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.jhotdraw.draw.Figure;

/**
 * IterableTree.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 * @param <T> the type of nodes in the iterable tree
 */
public interface IterableTree<T extends IterableTree<T>> {

    /**
     * Returns the children of the tree node.
     */
    List<T> getChildren();

    /**
     * Returns the parent of the tree node.
     */
    T getParent();

    /**
     * Returns an iterable which can iterate through this figure and all its
     * descendants in preorder sequence.
     *
     * @return the iterable
     */
    default public Iterable<T> preorderIterable() {
        @SuppressWarnings("unchecked")
        Iterable<T> i = () -> new IterableTree.PreorderIterator<>((T) this);
        return i;
    }

    /**
     * Returns an iterable which can iterate through this figure and all its
     * descendants in breadth first sequence.
     *
     * @return the iterable
     */
    default public Iterable<T> breadthFirstIterable() {
        @SuppressWarnings("unchecked")
        Iterable<T> i = () -> new IterableTree.BreadthFirstIterator<>((T) this);
        return i;
    }

    /**
     * Dumps the figure and its descendants to system.out.
     */
    default void dumpTree() {
        try {
            dumpTree(System.out, 0);
        } catch (IOException e) {
            throw new InternalError(e);
        }
    }

    /**
     * Dumps the figure and its descendants.
     *
     * @param out an output stream
     * @param depth the indentation depth
     * @throws java.io.IOException from appendable
     */
    default void dumpTree(Appendable out, int depth) throws IOException {
        for (int i = 0; i < depth; i++) {
            out.append('.');
        }
        out.append(toString());
        out.append('\n');
        for (T child : getChildren()) {
            child.dumpTree(out, depth + 1);
        }
    }

    static class PreorderIterator<T extends IterableTree<T>> implements Iterator<T> {

        private final LinkedList<Iterator<T>> stack = new LinkedList<>();

        private PreorderIterator(T root) {
            LinkedList<T> v = new LinkedList<>();
            v.add(root);
            stack.push(v.iterator());
        }

        @Override
        public boolean hasNext() {
            return (!stack.isEmpty() && stack.peek().hasNext());
        }

        @Override
        public T next() {
            Iterator<T> iter = stack.peek();
            T node = iter.next();
            Iterator<T> children = node.getChildren().iterator();

            if (!iter.hasNext()) {
                stack.pop();
            }
            if (children.hasNext()) {
                stack.push(children);
            }
            return node;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    static class BreadthFirstIterator<T extends IterableTree<T>> implements Iterator<T> {

        protected LinkedList<Iterator<T>> queue;

        public BreadthFirstIterator(T root) {
            List<T> l = new LinkedList<>();
            l.add(root);
            queue = new LinkedList<>();
            queue.addLast(l.iterator());
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty()
                    && queue.peekFirst().hasNext();
        }

        @Override
        public T next() {
            Iterator<T> iter = queue.peekFirst();
            T node = iter.next();
            Iterator<T> children = node.getChildren().iterator();

            if (!iter.hasNext()) {
                queue.removeFirst();
            }
            if (children.hasNext()) {
                queue.addLast(children);
            }
            return node;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * Returns the path to this node.
     *
     * @return path including this node
     */
    @SuppressWarnings("unchecked")
    default List<T> getPath() {
        LinkedList<T> path = new LinkedList<>();
        for (T node = (T) this; node != null; node = node.getParent()) {
            path.addFirst(node);
        }
        return path;
    }
}
