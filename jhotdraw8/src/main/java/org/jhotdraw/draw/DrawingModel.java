/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.draw;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import java.util.List;
import javafx.beans.Observable;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.event.Listener;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * {@code DrawingModel} provides {@code DrawingModelEvent}s about a
 * {@code Drawing}.
 * <p>
 * {@code DrawingModel} is used by {@code DrawingView}, {@code Tool},
 * {@code Handle} and all other kinds of inspectors to get change events
 * from the drawing without having to register listeners on all figures.
 * <p>
 * A {@code DrawingModel} might register listeners on all figures.
 * But since this might not scale well with large drawings, a 
 * {@code DrawingModel} typically infers the events from operations performed
 * on the figures through the {@code DrawingModel} API.
 * 
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DrawingModel extends Observable {

    /** Adds a listener form {@code DrawingModelEvent}s.
     *
     * @param l the listener */
    void addDrawingModelListener(Listener<DrawingModelEvent> l);

    /** Removes a listener form {@code DrawingModelEvent}s.
     *
     * @param l the listener */
    void removeDrawingModelListener(Listener<DrawingModelEvent> l);

    /** Gets the root of the tree.
     *
     * @return the drawing
     */
    Drawing getRoot();

    /** Sets the root of the tree.
     *
     * @param root the new root
     */
    void setRoot(Drawing root);

    /** Gets the children of parent.
     *
     * @param parent the parent.
     * @return list a read only list of the children.
     * For performance reason, this list is not always wrapped into
     * Collections.unmodifiableList, but should be treated as such.
     */
    List<Figure> getChildren(Figure parent);

    /** Gets the child count of parent.
     *
     * @param parent the parent.
     */
    default int getChildCount(Figure parent) {
        return getChildren(parent).size();
    }

    /** Gets the specified child of parent.
     *
     * @param parent the parent.
     * @param index the index.
     * @return the child
     */
    default Figure getChildAt(Figure parent, int index) {
        return getChildren(parent).get(index);
    }

    /** Removes the specified child from its parent.
     *
     * @param child the child
     */
    void removeFromParent(Figure child);

    /** Adds the specified child to a parent.
     *
     * @param child the new child
     * @param parent the parent.
     * @param index the index
     */
    void insertChildAt(Figure child, Figure parent, int index);
    /** Adds the specified child to a parent.
     *
     * @param child the new child
     * @param parent the parent.
     */
    default void addChildTo(Figure child, Figure parent) {
        insertChildAt(child,parent,getChildCount(parent));
    }
    
    

    /** Sets the specified property on the figure.
     *
     * @param <T> the value type
     * @param figure the figure
     * @param key the key
     * @param value the value
     */
    <T> void set(Figure figure, Key<T> key, T value);

    /** Gets the specified property from the figure.
     *
     * @param <T> the value type
     * @param figure the figure
     * @param key the key
     */
    <T> T get(Figure figure, Key<T> key);

    /**
     * Attempts to change the layout bounds of the figure.
     * <p>
     * The figure may choose to only partially change its layout bounds.
     * <p>
     * Reshape typically changes property values in this figure. The way how
     * this is performed is implementation specific.
     *
     * @param f the figure
     * @param transform the desired transformation
     */
    void reshape(Figure f, Transform transform);

    /**
     * Attempts to change the layout bounds of the figure.
     * <p>
     * Width and height are ignored, if the figure is not resizable.
     * <p>
     * If the layout bounds of the figure changes, it fires an invalidation
     * event.
     *
     * @param f the figure
     * @param x desired x-position
     * @param y desired y-position
     * @param width desired width, may be negative
     * @param height desired height, may be negative
     */
    void reshape(Figure f, double x, double y, double width, double height);
    
    /**
     * Retrieves the layout bounds of the figure.
     * <p>
     * FIXME If a figure performs layout operations, then this may cause the
     * figure to change property values of its children, which might cascade
     * into the entire subtree and connected connection figures. :-P
     */
    
    /** Invoke this method if you have made non-structural changes on a single
     * figure.
     * @param figure the figure
     * @param bits dirty bits describing the change
     */
    void fireFigureChanged(Figure figure, DirtyBits... bits);
    /** Invoke this method if you have made non-structural changes on a subtree
     * of figures.
     * @param figure the figure at the root of the subtree
     * @param bits dirty bits describing the change
     */
    void fireSubtreeNodesChanged(Figure figure, DirtyBits... bits);
    /** Invoke this method if you have made structural changes on a subtree
     * of figures.
     * @param figure the figure at the root of the subtree
     */
    void fireSubtreeStructureChanged(Figure figure);
}
