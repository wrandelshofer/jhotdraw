/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.draw.model;

import java.util.List;
import javafx.beans.Observable;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.event.Listener;

/**
 * {@code DrawingModel} provides {@code DrawingModelEvent}s about a
 * {@code Drawing}.
 * <p>
 * {@code DrawingModel} is used by {@code DrawingView} to get change events
 * from a drawing without having to register listeners on all figures.</p>
 * <p>
 * The {@code DrawingModelEvent}s that a {@code DrawingModel} fires are
 * based on assumptions that it makes about the figures contained in the
 * drawing. If the assumptions are wrong, then the drawing view will not
 * properly update its view!</p>
 * <p>
 * {@code DrawingModel} invokes {@code addNotify()} and {@code removeNotify()}
 * methods on a {@code Figure} when it detects that the figure has been
 * added or removed from a {@code Drawing}.
 * </p>
 * <p>
 * A {@code DrawingView} will only be updated properly, if all {@code Tool}s,
 * {@code Handle}s and inspectors update the drawing using the
 * {@code DrawingModel}.
 * </p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DrawingModel extends Observable {

    /** Adds a listener for {@code DrawingModelEvent}s.
     *
     * @param l the listener */
    void addDrawingModelListener(Listener<DrawingModelEvent> l);

    /** Removes a listener for {@code DrawingModelEvent}s.
     *
     * @param l the listener */
    void removeDrawingModelListener(Listener<DrawingModelEvent> l);

    /** Gets the root of the tree.
     *
     * @return the drawing
     */
    Drawing getRoot();

    /** Sets the root of the tree and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param root the new root
     */
    void setRoot(Drawing root);

    /** Gets the getChildren of the specified figure.
     *
     * @param figure the figure.
     * @return the getChildren.
     */
    default List<Figure> getChildren(Figure figure) {
        return figure.getChildren();
    }

    /** Gets the child count of the specified figure.
     *
     * @param figure the parent.
     * @return the number of getChildren
     */
    default int getChildCount(Figure figure) {
        return getChildren(figure).size();
    }

    /** Gets the child at the given index from the parent.
     *
     * @param parent the parent.
     * @param index the index.
     * @return the child
     */
    default Figure getChildAt(Figure parent, int index) {
        return getChildren(parent).get(index);
    }

    /** Removes the specified child from its parent and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param child the figure
     */
    void removeFromParent(Figure child);

    /** Adds the specified child to a parent and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param child the new child
     * @param parent the parent.
     * @param index the index
     */
    void insertChildAt(Figure child, Figure parent, int index);

    /** Adds the specified child to a parent and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param child the new child
     * @param parent the parent.
     */
    default void addChildTo(Figure child, Figure parent) {
        insertChildAt(child, parent, getChildCount(parent));
    }

    /** Sets the specified property on the figure and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param <T> the value type
     * @param figure the figure
     * @param key the key
     * @param newValue the new value
     * @return the old value
     */
    <T> T set(Figure figure, Key<T> key, T newValue);

    /** Gets the specified property from the figure.
     *
     * @param <T> the value type
     * @param figure the figure
     * @param key the key
     * @return the value
     */
    default <T> T get(Figure figure, Key<T> key) {
        return figure.get(key);
    }

    /**
     * Attempts to change the layout bounds of the figure and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param f the figure
     * @param transform the desired transformation
     */
    void reshape(Figure f, Transform transform);

    /**
     * Attempts to change the layout bounds of the figure and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param f the figure
     * @param x desired x-position
     * @param y desired y-position
     * @param width desired width, may be negative
     * @param height desired height, may be negative
     */
    void reshape(Figure f, double x, double y, double width, double height);

    /**
     * Invokes the layout method of the figure and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param f the figure
     */
    void layout(Figure f);
    /**
     * Invokes the disconnect method of the figure and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param f the figure
     */
    void disconnect(Figure f);
    /**
     * Invokes the applyCss method of the figure and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param f the figure
     */
    void applyCss(Figure f);

    /**
     * Fires the specified event.
     * @param event the event
     */
    void fire(DrawingModelEvent event);
    
    /** Validates the model. 
     * This method is invoked by {@code DrawingView} each time before it renders
     * the model.
     */
    void validate();
    
    // ---
    // convenience methods
    // ---
    /**
     * Fires "node invalidated" event for the specified figure.
     * @param f the figure
     */
    default void fireNodeInvalidated(Figure f) {
        fire(DrawingModelEvent.nodeInvalidated(this, f));
    }
    /**
     * Fires "node invalidated" event for the specified figure.
     * @param f the figure
     */
    default void fireTransformInvalidated(Figure f) {
        fire(DrawingModelEvent.transformChanged(this, f));
    }
    /**
     * Fires "node invalidated" event for the specified figure.
     * @param f the figure
     */
    default void fireLayoutInvalidated(Figure f) {
        fire(DrawingModelEvent.layoutInvalidated(this, f));
    }
}
