/* @(#)TreeNode.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.tree;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a node of a tree structure.
 * <p>
 * A node has zero or one parents, and zero or more children.
 * <p>
 * All nodes in the same tree structure are of the same type {@literal <T>}.
 * <p>
 * A node may support only a restricted set of parent types
 * {@literal <P extends T>}.
 * <p>
 * A node may only support a restricted set of child types
 * {@literal <C extends T>}.
 * <p>
 * The type {@literal <T>} is checked at compile time using a Java type
 * parameter. The types {@literal <P>} and {@literal <C>} are checked at
 * runtime.
 *
 * @design.pattern TreeNode Composite, Component. The composite pattern is used
 * to model a tree structure.
 *
 * @design.pattern TreeNode Iterator, Aggregate. The iterator pattern is used to
 * provide a choice of iteration strategies over an aggregate structure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <T> the type of nodes in the tree structure.
 */
public interface TreeNode<T extends TreeNode<T>> {

    /**
     * Returns an iterable which can iterate through this figure and all its
     * ancesters up to the root.
     *
     * @return the iterable
     */
    default Iterable<T> ancestorIterable() {
        @SuppressWarnings("unchecked")
        Iterable<T> i = () -> new TreeNode.AncestorIterator<>((T) this);
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
        Iterable<T> i = () -> new TreeNode.BreadthFirstIterator<>((T) this);
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

    /**
     * Returns the nearest ancestor of the specified type.
     *
     * @param <TT> The ancestor type
     * @param ancestorType The ancestor type
     * @return Nearest ancestor of type {@literal <T>} or null if no ancestor of
     * this type is present. Returns {@code this} if this object is of type
     * {@literal <T>}.
     */
    default <TT> TT getAncestor(Class<TT> ancestorType) {
        @SuppressWarnings("unchecked")
        T ancestor = (T) this;
        while (ancestor != null && !ancestorType.isAssignableFrom(ancestor.getClass())) {
            ancestor = ancestor.getParent();
        }
        @SuppressWarnings("unchecked")
        TT temp = (TT) ancestor;
        return temp;
    }

    /**
     * Gets the child with the specified index from the node.
     *
     * @param index the index
     * @return the child
     */
    default T getChild(int index) {
        return getChildren().get(index);
    }

    /**
     * Sets the child with the specified index from the node.
     *
     * @param index the index
     * @param newChild the new child
     * @return the old child
     */
    default T setChild(int index, T newChild) {
        return getChildren().set(index, newChild);
    }

    /**
     * Returns the children of the tree node.
     * <p>
     * In order to keep the tree structure consistent, the following rules must
     * be followed:
     * <ul>
     * <li>If a child is added to this list, then it must be removed from its
     * former parent, and this this tree node must be set as the parent of the
     * child.</li>
     * <li>
     * If a child is removed from this tree node, then the parent of the child
     * must be set to null.</li>
     * </ul>
     *
     * @return the children
     */
    List<T> getChildren();

    /**
     * Gets the first child.
     *
     * @return The first child. Returns null if the figure has no getChildren.
     */
    default T getFirstChild() {
        return getChildren().isEmpty() //
                ? null//
                : getChildren().get(getChildren().size() - 1);
    }

    /**
     * Gets the last child.
     *
     * @return The last child. Returns null if the figure has no getChildren.
     */
    default T getLastChild() {
        return getChildren().isEmpty() ? null : getChildren().get(0);
    }

    /**
     * Returns the parent of the tree node.
     * <p>
     * Note that - by convention - the parent property is changed only by a
     * parent tree node.
     *
     * @return the parent. Returns null if the tree node has no parent.
     */
    T getParent();

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

    /**
     * Returns an iterable which can iterate through this figure and all its
     * descendants in postorder sequence.
     *
     * @return the iterable
     */
    default public Iterable<T> postorderIterable() {
        @SuppressWarnings("unchecked")
        Iterable<T> i = () -> new TreeNode.PostorderIterator<>((T) this);
        return i;
    }

    /**
     * Returns an iterable which can iterate through this figure and all its
     * descendants in preorder sequence.
     *
     * @return the iterable
     */
    default public Iterable<T> preorderIterable() {
        @SuppressWarnings("unchecked")
        Iterable<T> i = () -> new TreeNode.PreorderIterator<>((T) this);
        return i;
    }

    /**
     * @design.pattern TreeNode Iterator, Iterator.
     *
     * @param <T> the type of the tree nodes
     */
    static class AncestorIterator<T extends TreeNode<T>> implements Iterator<T> {

        private T node;

        private AncestorIterator(T node) {
            this.node = node;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public T next() {
            T next = node;
            node = node.getParent();
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    /**
     * @param <T> the tree node type
     * @design.pattern TreeNode Iterator, Iterator.
     */
    static class BreadthFirstIterator<T extends TreeNode<T>> implements Iterator<T> {

        protected Deque<Iterator<T>> queue = new ArrayDeque<>();

        public BreadthFirstIterator(T root) {
            queue.addLast(Collections.singleton(root).iterator());
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
     * @design.pattern TreeNode Iterator, Iterator.
     *
     * @param <T> the type of the tree nodes
     */
    static class PostorderIterator<T extends TreeNode<T>> implements Iterator<T> {

        private T root;
        private Iterator<T> subtree;
        private Iterator<T> children;

        private PostorderIterator(T root) {
            this.root = root;
            children = root.getChildren().iterator();
            subtree = Collections.emptyIterator();
        }

        @Override
        public boolean hasNext() {
            return root != null;
        }

        @Override
        public T next() {
            T result;
            if (subtree.hasNext()) {
                result = subtree.next();
            } else if (children.hasNext()) {
                subtree = new PostorderIterator<>(children.next());
                result = subtree.next();
            } else {
                result = root;
                root = null;
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * @design.pattern TreeNode Iterator, Iterator.
     *
     * @param <T> the type of the tree nodes
     */
    static class PreorderIterator<T extends TreeNode<T>> implements Iterator<T> {

        private final Deque<Iterator<T>> stack = new ArrayDeque<>();

        private PreorderIterator(T root) {
            stack.push(Collections.singleton(root).iterator());
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



}
