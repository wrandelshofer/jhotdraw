/* @(#)TreeModelEvent.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.tree;

import javax.annotation.Nonnull;

import org.jhotdraw8.event.Event;

/**
 * TreeModelEvent.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TreeModelEvent<E> extends Event<TreeModel<E>> {

    private final static long serialVersionUID = 1L;

    public enum EventType {
        /**
         * The root of the model changed.
         */
        ROOT_CHANGED,
        /**
         * All JavaFX Nodes in a subtree of the figures have been invalidated.
         */
        SUBTREE_NODES_CHANGED,
        /**
         * A subtree of figures  has been added to a parent.
         * <p>
 The subtree of figures is already part of the root, and has just been removed from another parent.
         */
        NODE_ADDED_TO_PARENT,
        /**
         * A subtree of figures has been removed from its parent.
         * <p>
 The subtree of figures is still part of the root, and is about to be added to another parent.
         */
        NODE_REMOVED_FROM_PARENT,
        /**
         * A subtree of figures has been added to the root.
         * <p>
 The subtree of figures has become part of the root.
 This event is fired, before NODE_ADDED_TO_PARENT is fired.
         */
        NODE_ADDED_TO_TREE,
        /**
         * A subtree of figures has been removed from the root.
         * <p>
 The subtree of figures is no longer part of the root.
 This event is fired, after NODE_REMOVED_FROM_PARENT is fired.
         */
        NODE_REMOVED_FROM_TREE,
        /**
         * The JavaFX Node of a single figure has been invalidated.
         */
        NODE_CHANGED,

    }
    private final E node;

    private final E parent;
    private final E root;
    private final int index;
    private final TreeModelEvent.EventType eventType;

    private TreeModelEvent(TreeModel<E> source, EventType eventType, E node, E parent, E root, int index) {
        super(source);
        this.node = node;
        this.parent = parent;
        this.root = root;
        this.index = index;
        this.eventType = eventType;
    }

    public static <E> TreeModelEvent<E> subtreeNodesInvalidated(TreeModel<E> source, E subtreeRot) {
        return new TreeModelEvent<>(source, EventType.SUBTREE_NODES_CHANGED, subtreeRot, null, null, -1);
    }

    public static<E>  TreeModelEvent<E> nodeAddedToParent(TreeModel<E> source, E child, E parent, int index) {
        return new TreeModelEvent<>(source, EventType.NODE_ADDED_TO_PARENT, child, parent, null, index);
    }

    public static <E> TreeModelEvent<E> nodeRemovedFromParent(TreeModel<E> source, E child, E parent, int index) {
        return new TreeModelEvent<>(source, EventType.NODE_REMOVED_FROM_PARENT, child, parent, null, index);
    }

    public static <E> TreeModelEvent<E> nodeAddedToTree(TreeModel<E> source, E root, E node) {
        return new TreeModelEvent<>(source, EventType.NODE_ADDED_TO_TREE, node, null, root, -1);
    }

    public static <E> TreeModelEvent<E> nodeRemovedFromTree(TreeModel<E> source, E root, E node) {
        return new TreeModelEvent<>(source, EventType.NODE_REMOVED_FROM_TREE, node, null, root, -1);
    }

    public static <E> TreeModelEvent<E> nodeInvalidated(TreeModel<E> source, E node) {
        return new TreeModelEvent<>(source, EventType.NODE_CHANGED, node, null, null, -1);
    }

    public static <E> TreeModelEvent<E> rootChanged(TreeModel<E> source, E root) {
        return new TreeModelEvent<>(source, EventType.ROOT_CHANGED, root, null, null, -1);
    }

    /**
     * The figure which was added, removed or of which a property changed.
     *
     * @return the figure
     */
    public E getNode() {
        return node;
    }

    /**
     * If a child was added or removed from a parent, returns the parent.
     *
     * @return the parent
     */
    public E getParent() {
        return parent;
    }

    /**
     * If a child was added or removed from a root, returns the root.
     *
     * @return the root
     */
    public E getRoot() {
        return root;
    }

    /**
     * If a child was added or removed, returns the child.
     *
     * @return the child
     */
    public E getChild() {
        return node;
    }

    /**
     * If the figure was added or removed, returns the child index.
     *
     * @return an index. Returns -1 if the figure was neither added or removed.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the event type.
     *
     * @return the event type
     */
    public TreeModelEvent.EventType getEventType() {
        return eventType;
    }

    @Nonnull
    @Override
    public String toString() {
        return "TreeModelEvent{"
                +"node="+node
                + ", parent=" + parent 
                + ", index=" + index + ", eventType="
                + eventType + ", source=" + source + '}';
    }


}
