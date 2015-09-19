/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.draw;

import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.MapChangeListener;
import javafx.scene.transform.Transform;
import org.jhotdraw.beans.ListenerSupport;
import org.jhotdraw.collection.Key;
import org.jhotdraw.event.Listener;

/**
 * SimpleDrawingModel.
 * <p>
 * For performance reasons this model does not register listeners on the
 * figures. Thus listeners are only informed about changes on operations
 * performed through this interface.
 * <p>
 * The events that this model fires are based on assumptions of the
 * figure classes defined in the {@code org.jhotdraw.draw} packages.
 * Specifically
 * for composite figures based on {@code AbstractCompositeFigure}
 * and connectable figures based on {@code ConnectableFigure} and
 * {@code ConnectionFigure}. If, for example, you define a new kind
 * of connectable figures which are not based on the @code ConnectableFigure}
 * you may have to implement a new DrawingModel class which generates
 * the proper events for {@code DrawingModelEvent} listeners.
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawingModel implements DrawingModel {

    private final ListenerSupport<Listener<DrawingModelEvent>> dmeListeners = new ListenerSupport<>();
    private final ListenerSupport<InvalidationListener> invalidationListeners = new ListenerSupport<>();

    private final MapChangeListener<Key<?>, Object> propertyHandler = new MapChangeListener<Key<?>, Object>() {

        @Override
        public void onChanged(MapChangeListener.Change<? extends Key<?>, ? extends Object> change) {
            fire(DrawingModelEvent.propertyChanged(//
                    SimpleDrawingModel.this,
                    (Figure) ((ReadOnlyMapProperty) change.getMap()).getBean(),//
                    (Key<Object>) change.getKey(), //
                    (Object) change.getValueRemoved(), //
                    (Object) change.getValueAdded()
            ));

        }
    };

    /** The root. */
    private Drawing root = new SimpleDrawing();

    @Override
    public void addDrawingModelListener(Listener<DrawingModelEvent> l) {
        dmeListeners.addListener(l);
    }

    @Override
    public void removeDrawingModelListener(Listener<DrawingModelEvent> l) {
        dmeListeners.removeListener(l);
    }

    @Override
    public void addListener(InvalidationListener l) {
        invalidationListeners.addListener(l);
    }

    @Override
    public void removeListener(InvalidationListener l) {
        invalidationListeners.removeListener(l);
    }

    @Override
    public Drawing getRoot() {
        return root;
    }

    @Override
    public List<Figure> getChildren(Figure parent) {
        return parent.children();
    }

    @Override
    public void removeFromParent(Figure child) {
        Figure parent = child.getParent();
        int index = parent.children().indexOf(child);
        parent.children().remove(index);
        { // fire child added
            fire(DrawingModelEvent.figureRemoved(this, parent, child, index));

        }
        { // we assume that the node of the parent figure needs to be updated
            fire(DrawingModelEvent.nodeChanged(this, parent));

        }
        fireInvalidated();
    }

    @Override
    public void insertChildAt(Figure child, Figure parent, int index) {
        parent.children().add(index, child);
        { // fire child removed
            fire(DrawingModelEvent.figureAdded(this, parent, child, index));

        }
        { // we assume that the node of the parent figure needs to be updated
            fire(DrawingModelEvent.nodeChanged(this, parent));

        }
        fireInvalidated();
    }

    @Override
    public <T> void set(Figure figure, Key<T> key, T newValue) {
        T oldValue = figure.set(key, newValue);
        { // fire property change
            fire(DrawingModelEvent.propertyChanged(this, figure, key, oldValue, newValue));

        }
        if (key instanceof FigureKey) {
            DirtyMask dm = ((FigureKey<T>) key).getDirtyMask();
            if (dm.containsOneOf(DirtyBits.NODE)) {
                // we assume that the node needs to be updated when the
                // corresponding bit in the dirty mask is set
                fire(DrawingModelEvent.nodeChanged(this, figure));

            }

            if (figure instanceof ConnectableFigure) {
                ConnectableFigure connectable = (ConnectableFigure) figure;
                if (dm.containsOneOf(DirtyBits.GEOMETRY, DirtyBits.LAYOUT_BOUNDS)) {
                    for (ConnectionFigure connection : connectable.connections()) {
                        // we assume that the nodes of all connections need
                        // to be updated when the geometry or the layout bounds
                        // of a connectable figure changes.
                        fire(DrawingModelEvent.nodeChanged(this, connection));

                    }
                }
            }
        }
        fireInvalidated();
    }

    @Override
    public <T> T get(Figure figure, Key<T> key) {
        return figure.get(key);
    }

    @Override
    public void reshape(Figure figure, Transform transform) {
        // we assume that reshaping a figure results in property changes
        // so we register a listener here and unregister it immediately afterwards.
        figure.properties().addListener(propertyHandler);
        figure.reshape(transform);
        figure.properties().removeListener(propertyHandler);

        fireSubtreeNodesChanged(figure, DirtyBits.NODE, DirtyBits.LAYOUT_BOUNDS);
        fireInvalidated();
    }

    @Override
    public void reshape(Figure figure, double x, double y, double width, double height) {
        // we assume that reshaping a figure results in various property changes
        // so we register a listener here and unregister it immediately afterwards.
        figure.properties().addListener(propertyHandler);

        figure.reshape(x, y, width, height);

        // we assume that reshaping a figure results in property changes
        // so we register a listener here and unregister it immediately afterwards.
        figure.properties().removeListener(propertyHandler);

        fireSubtreeNodesChanged(figure, DirtyBits.NODE, DirtyBits.LAYOUT_BOUNDS);
        fireInvalidated();
    }

    @Override
    public void setRoot(Drawing newValue) {
        Drawing oldValue = root;
        root = newValue;
        fire(DrawingModelEvent.rootChanged(this, newValue));
    }

    @Override
    public void fireFigureChanged(Figure figure, DirtyBits... bits) {
        DirtyMask mask = DirtyMask.of(bits);
        if (mask.containsOneOf(DirtyBits.NODE)) {
            fire(DrawingModelEvent.nodeChanged(this, figure));

        }
        if (mask.containsOneOf(DirtyBits.LAYOUT_BOUNDS, DirtyBits.GEOMETRY)) {
            if (figure instanceof ConnectableFigure) {
                ConnectableFigure connectable = (ConnectableFigure) figure;
                for (ConnectionFigure connection : connectable.connections()) {
                    // we assume that the nodes of all connections need
                    // to be updated when the geometry or the layout bounds
                    // of a connectable figure changes.
                    fire(DrawingModelEvent.nodeChanged(this, connection));

                }
            }
        }
        fireInvalidated();
    }

    @Override
    public void fireSubtreeNodesChanged(Figure figure, DirtyBits... bits) {
        DirtyMask mask = DirtyMask.of(bits);
        fire(DrawingModelEvent.subtreeNodesChanged(this, figure));
        if (mask.containsOneOf(DirtyBits.LAYOUT_BOUNDS, DirtyBits.GEOMETRY)) {
            for (Figure f : figure.preorderIterable()) {
                if (f instanceof ConnectableFigure) {
                    ConnectableFigure connectable = (ConnectableFigure) f;
                    for (ConnectionFigure connection : connectable.connections()) {
                        // we assume that the nodes of all connections need
                        // to be updated when the geometry or the layout bounds
                        // of a connectable figure changes.
                        fire(DrawingModelEvent.nodeChanged(this, connection));

                    }
                }
            }
        }
        fireInvalidated();
    }

    @Override
    public void fireSubtreeStructureChanged(Figure figure) {
        fire(DrawingModelEvent.subtreeStructureChanged(this, figure));
        for (Figure f : figure.preorderIterable()) {
            if (f instanceof ConnectableFigure) {
                ConnectableFigure connectable = (ConnectableFigure) f;
                for (ConnectionFigure connection : connectable.connections()) {
                        // we assume that the nodes of all connections need
                    // to be updated when the geometry or the layout bounds
                    // of a connectable figure changes.
                    fire(DrawingModelEvent.nodeChanged(this, connection));

                }
            }
        }
        fireInvalidated();
    }

    private void fireInvalidated() {
        invalidationListeners.fire(l -> l.invalidated(this));

    }

    private void fire(DrawingModelEvent event) {
        dmeListeners.fire(l -> l.handle(event));
    }

}
