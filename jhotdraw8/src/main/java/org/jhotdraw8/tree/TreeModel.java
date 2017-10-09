/* @(#)TreeModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.tree;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javax.annotation.Nonnull;
import org.jhotdraw8.beans.ObservableMixin;
import org.jhotdraw8.event.Listener;

/**
 * TreeModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface TreeModel<E> extends ObservableMixin {
    /**
     * Name of the root property.
     */
    String ROOT_PROPERTY = "root";

    /**
     * List of drawing model listeners.
     *
     * @return a list of drawing model listeners
     */
    CopyOnWriteArrayList<Listener<TreeModelEvent<E>>> getTreeModelListeners();

    /**
     * List of invalidation listeners.
     *
     * @return a list of drawing model listeners
     */
    CopyOnWriteArrayList<InvalidationListener> getInvalidationListeners();

    /**
     * The root of the drawing model.
     *
     * @return the root
     */
    ObjectProperty<E> rootProperty();

    /**
     * Adds a listener for {@code TreeModelEvent<E>}s.
     *
     * @param l the listener
     */
    default void addTreeModelListener(@Nonnull Listener<TreeModelEvent<E>> l) {
        getTreeModelListeners().add(l);
    }

    /**
     * Removes a listener for {@code TreeModelEvent<E>}s.
     *
     * @param l the listener
     */
    default void removeTreeModelListener(@Nonnull Listener<TreeModelEvent<E>> l) {
        getTreeModelListeners().remove(l);
    }

    /**
     * Gets the root of the tree.
     *
     * @return the drawing
     */@Nonnull 
    default E getRoot() {
        return rootProperty().get();
    }

    /**
     * Sets the root of the tree and fires appropriate
     * {@code TreeModelEvent<E>}s.
     *
     * @param root the new root
     */
    default void setRoot(@Nonnull E root) {
        rootProperty().set(root);
    }

    /**
     * Gets the children of the specified node.
     *
     * @param node the node.
     * @return the getChildren.
     */@Nonnull 
    List<E> getChildren(@Nonnull E node);

    /**
     * Gets the child count of the specified figure.
     *
     * @param node the parent.
     * @return the number of getChildren
     */
    int getChildCount(@Nonnull E node);

    /**
     * Gets the child at the given index from the parent.
     *
     * @param parent the parent.
     * @param index the index.
     * @return the child
     */@Nonnull 
    E getChildAt(@Nonnull E parent, int index);

    /**
     * Removes the specified child from its parent and fires appropriate
     * {@code TreeModelEvent<E>}s.
     *
     * @param child the child
     */
    void removeFromParent(@Nonnull E child);

    /**
     * Adds the specified child to a parent and fires appropriate
     * {@code TreeModelEvent<E>}s.
     *
     * @param child the new child
     * @param parent the parent.
     * @param index the index
     */
    void insertChildAt(@Nonnull E child, @Nonnull E parent, int index);

    /**
     * Adds the specified child to a parent and fires appropriate
     * {@code TreeModelEvent<E>}s.
     *
     * @param child the new child
     * @param parent the parent.
     */
    default void addChildTo(@Nonnull E child, @Nonnull E parent) {
        insertChildAt(child, parent, getChildCount(parent));
    }

    /**
     * Fires the specified event.
     *
     * @param event the event
     */
    default void fireTreeModelEvent(@Nonnull TreeModelEvent<E> event) {
       for (Listener<TreeModelEvent<E>> l : getTreeModelListeners()) {
           l.handle(event);
       }
    }

    // ---
    // convenience methods
    // ---
    /**
     * Fires "node invalidated" event for the specified node.
     *
     * @param f the figure
     */
    default void fireNodeInvalidated(@Nonnull E f) {
        fireTreeModelEvent(TreeModelEvent.nodeInvalidated(this, f));
    }

}
