/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.draw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.event.Listener;

/**
 * {@code DrawingModel} provides {@code DrawingModelEvent}s about a
 * {@code Drawing}.
 * <p>
 * {@code DrawingModel} is used by {@code DrawingView} to get change events from
 * a drawing without having to register listeners on all figures.</p>
 * <p>
 * The {@code DrawingModelEvent}s that a {@code DrawingModel} fires are based on
 * assumptions that it makes about the figures contained in the drawing. If the
 * assumptions are wrong, then the drawing view will not properly update its
 * view!</p>
 * <p>
 * {@code DrawingModel} invokes {@code addNotify()} and {@code removeNotify()}
 * methods on a {@code Figure} when it detects that the figure has been added or
 * removed from a {@code Drawing}.
 * </p>
 * <p>
 * A {@code DrawingView} will only be updated properly, if all {@code Tool}s,
 * {@code Handle}s and inspectors update the drawing using the
 * {@code DrawingModel}.
 * </p>
 *
 * @design.pattern DrawingModel Facade, Facade. {@code DrawingModel} acts as a
 * facade for the internal structure of a {@code Drawing} (a Drawing is composed
 * of a tree of {@code Figure} objects). DrawingModel provides methods for
 * altering the tree structure, for setting and getting property values of
 * Figure objects, and provides a single point for registering listeners which
 * need to observe changes of Figures in the tree structure.
 *
 * @design.pattern DrawingModel Strategy, Strategy. The strategy for updating
 * the state of dependent {@link Figure} objects is implemented in
 * {@link DrawingModel}. {@code DrawingModel} uses
 * {@link org.jhotdraw8.draw.key.DirtyBits} as a hint for its strategy.
 *
 * @design.pattern DrawingModel MVC, Model. The model view controller (MVC)
 * pattern is used to decouple application code from user interface code. See {@link DrawingModel},
 * {@link org.jhotdraw8.draw.DrawingView} and
 * {@link org.jhotdraw8.draw.tool.Tool}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DrawingModel extends Observable {

    /**
     * Name of the root property.
     */
    String ROOT_PROPERTY = "root";

    /**
     * List of drawing model listeners.
     *
     * @return a list of drawing model listeners
     */
    CopyOnWriteArrayList<Listener<DrawingModelEvent>> getDrawingModelListeners();

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
    ObjectProperty<Drawing> rootProperty();

    /**
     * Adds a listener for {@code DrawingModelEvent}s.
     *
     * @param l the listener
     */
    default void addDrawingModelListener(Listener<DrawingModelEvent> l) {
        getDrawingModelListeners().add(l);
    }

    /**
     * Removes a listener for {@code DrawingModelEvent}s.
     *
     * @param l the listener
     */
    default void removeDrawingModelListener(Listener<DrawingModelEvent> l) {
        getDrawingModelListeners().remove(l);
    }

    @Override
    default void addListener(InvalidationListener l) {
        getInvalidationListeners().add(l);
    }

    @Override
    default void removeListener(InvalidationListener l) {
        getInvalidationListeners().remove(l);
    }

    /**
     * Gets the root of the tree.
     *
     * @return the drawing
     */
    default Drawing getRoot() {
        return rootProperty().get();
    }

    /**
     * Sets the root of the tree and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param root the new root
     */
    default void setRoot(Drawing root) {
        rootProperty().set(root);
    }

    /**
     * Gets the getChildren of the specified figure.
     *
     * @param figure the figure.
     * @return the getChildren.
     */
    default List<Figure> getChildren(Figure figure) {
        return figure.getChildren();
    }

    /**
     * Gets the child count of the specified figure.
     *
     * @param figure the parent.
     * @return the number of getChildren
     */
    default int getChildCount(Figure figure) {
        return getChildren(figure).size();
    }

    /**
     * Gets the child at the given index from the parent.
     *
     * @param parent the parent.
     * @param index the index.
     * @return the child
     */
    default Figure getChildAt(Figure parent, int index) {
        return getChildren(parent).get(index);
    }

    /**
     * Removes the specified child from its parent and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param child the figure
     */
    void removeFromParent(Figure child);

    /**
     * Adds the specified child to a parent and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param child the new child
     * @param parent the parent.
     * @param index the index
     */
    void insertChildAt(Figure child, Figure parent, int index);

    /**
     * Adds the specified child to a parent and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param child the new child
     * @param parent the parent.
     */
    default void addChildTo(Figure child, Figure parent) {
        insertChildAt(child, parent, getChildCount(parent));
    }

    /**
     * Sets the specified property on the figure and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param <T> the value type
     * @param figure the figure
     * @param key the key
     * @param newValue the new value
     * @return the old value
     */
    <T> T set(Figure figure, MapAccessor<T> key, T newValue);

    /**
     * Gets the specified property from the figure.
     *
     * @param <T> the value type
     * @param figure the figure
     * @param key the key
     * @return the value
     */
    default <T> T get(Figure figure, MapAccessor<T> key) {
        return figure.get(key);
    }

    /**
     * Attempts to change the local bounds of the figure and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param f the figure
     * @param transform the desired transformation in the local coordinate
     * system
     */
    void reshapeInLocal(Figure f, Transform transform);

    /**
     * Attempts to change the parent bounds of the figure and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param f the figure
     * @param transform the desired transformation in the parent coordinate
     * system
     */
    void reshapeInParent(Figure f, Transform transform);

    /**
     * Attempts to change the local bounds of the figure and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param f the figure
     * @param x desired x-position in the local coordinate system
     * @param y desired y-position in the local coordinate system
     * @param width desired width in the local coordinate system, may be
     * negative
     * @param height desired height in the local coordinat system, may be
     * negative
     */
    void reshape(Figure f, double x, double y, double width, double height);

    /**
     * Invokes the layout method of the figure and fires appropriate /
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
     * Invokes the updateCss method of the figure and fires appropriate
     * {@code DrawingModelEvent}s.
     *
     * @param f the figure
     */
    void updateCss(Figure f);

    /**
     * Fires the specified event.
     *
     * @param event the event
     */
    void fire(DrawingModelEvent event);

    /**
     * Validates the model. This method is invoked by {@code DrawingView} each
     * time before it renders the model.
     */
    void validate();

    // ---
    // convenience methods
    // ---
    /**
     * Fires "node invalidated" event for the specified figure.
     *
     * @param f the figure
     */
    default void fireNodeInvalidated(Figure f) {
        fire(DrawingModelEvent.nodeInvalidated(this, f));
    }

    /**
     * Fires "node invalidated" event for the specified figure.
     *
     * @param <T> the value type
     * @param f the figure
     * @param key the property key
     * @param oldValue the old value
     * @param newValue the new value
     */
    default <T> void firePropertyValueChanged(Figure f, Key<T> key, T oldValue, T newValue) {
        fire(DrawingModelEvent.propertyValueChanged(this, f, key, oldValue, newValue));
    }

    /**
     * Fires "node invalidated" event for the specified figure.
     *
     * @param f the figure
     */
    default void fireTransformInvalidated(Figure f) {
        fire(DrawingModelEvent.transformChanged(this, f));
    }

    /**
     * Fires "node invalidated" event for the specified figure.
     *
     * @param f the figure
     */
    default void fireLayoutInvalidated(Figure f) {
        fire(DrawingModelEvent.layoutChanged(this, f));
    }

    /**
     * Fires "style invalidated" event for the specified figure.
     *
     * @param f the figure
     */
    default void fireStyleInvalidated(Figure f) {
        fire(DrawingModelEvent.styleInvalidated(this, f));
    }

    /**
     * Fires an "invalidated" event.
     */
    default void fireDrawingModelInvalidated() {
        for (InvalidationListener l : new ArrayList<>(getInvalidationListeners())) {
            l.invalidated(this);
        }
    }

    /**
     * Invokes "transformInParent" on the specified figure.
     *
     * @param figure a figure
     * @param transform the desired transformation
     */
    public void transformInParent(Figure figure, Transform transform);

    /**
     * Invokes "transformInLocal" on the specified figure.
     *
     * @param figure a figure
     * @param transform the desired transformation
     */
    public void transformInLocal(Figure figure, Transform transform);

    /**
     * Removes the specified key from the figure.
     *
     * @param <T> the value type
     * @param f a figure
     * @param remove a key
     * @return the old value 
     */
    public <T> T remove(Figure f, Key<T> remove);
}
