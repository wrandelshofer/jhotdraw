/* @(#)SimpleDrawingModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.LinkedList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import org.jhotdraw.collection.Key;
import org.jhotdraw.event.Listener;
import javax.swing.tree.DefaultTreeModel;

/**
 * The {@code SimpleDrawingModel} listens to mutations on a figure and
 * all its descendants, and generates {@code DrawingMutationEvent}s.
 * 
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawingModel implements DrawingModel {

    private class FigureHandler implements ListChangeListener<Figure>, MapChangeListener<Key<?>, Object>, InvalidationListener {

        public FigureHandler() {
        }

        public void addFigure(Figure figure) {
            figure.childrenProperty().addListener((ListChangeListener<Figure>) this);
            figure.properties().addListener((MapChangeListener<Key<?>, Object>) this);
            figure.addListener((InvalidationListener) this);
        }

        public void removeFigure(Figure figure) {
            figure.childrenProperty().removeListener((ListChangeListener<Figure>) this);
            figure.properties().removeListener((MapChangeListener<Key<?>, Object>) this);
            figure.removeListener((InvalidationListener) this);
        }

        @Override
        public void onChanged(ListChangeListener.Change<? extends Figure> c) {
            Figure figure = (Figure) ((ReadOnlyProperty) c.getList()).getBean();
            while (c.next()) {
                final int from = c.getFrom();
                final int to = c.getTo();
                final ObservableList<? extends Figure> list = c.getList();
                if (c.wasPermutated()) {
                    // Fixme this is extremely slow, we should forward permutations
                    Figure[] tmp = new Figure[to - from];
                    for (int oldi = from; oldi < to; ++oldi) {
                        tmp[oldi] = list.get(oldi);
                        fireFigureRemoved(figure, tmp[oldi], oldi);
                    }
                    for (int i = from; i < to; ++i) {
                        for (int j = from; j < to; ++j) {
                            if (c.getPermutation(j) == i) {
                                fireFigureAdded(figure, tmp[j], i);
                            }
                        }
                    }

                } else if (c.wasUpdated()) {
                    //update item
                } else {
                    if (c.wasRemoved()) {
                        final List<? extends Figure> removed = c.getRemoved();
                        for (int i = 0, n = removed.size(); i < n; i++) {
                            handleFigureRemoved(removed.get(i));
                            fireFigureRemoved(figure, removed.get(i), i + from);
                        }
                    }
                    if (c.wasAdded()) {
                        for (int i = from; i < to; i++) {
                            handleFigureAdded(list.get(i));
                            fireFigureAdded(figure, list.get(i), i);
                        }
                    }
                }
            }
        }

        @Override
        public void onChanged(MapChangeListener.Change<? extends Key<?>, ? extends Object> change
        ) {
            Figure figure = (Figure) ((ReadOnlyProperty) change.getMap()).getBean();
            firePropertyChange(figure, (Key<Object>) change.getKey(), change.getValueRemoved(), change.getValueAdded());
        }

        @Override
        public void invalidated(Observable observable) {
            Figure figure = (Figure) observable;
            fireFigureInvalidated(figure);
        }

    }

    private Figure root;
    private final FigureHandler handler = new FigureHandler();

    public SimpleDrawingModel() {
        this(null);
    }
    public SimpleDrawingModel(Figure root) {
        setRoot(root);
    }


    @Override
    public void setRoot(Figure newValue) {
        if (root != null) {
            handleFigureRemoved(root);
        }
        root = newValue;
        if (root != null) {
            handleFigureAdded(root);
        }
    }

    private final LinkedList<Listener<DrawingModelEvent>> drawingModelListeners = new LinkedList<>();
    private final LinkedList<Listener<DrawingModelEvent>> propertyListeners = new LinkedList<>();
    private final LinkedList<InvalidationListener> invalidationListeners = new LinkedList<>();

    @Override
    public void addDrawingModelListener(Listener<DrawingModelEvent> listener) {
        drawingModelListeners.add(listener);
    }

    @Override
    public void removeDrawingModelListener(Listener<DrawingModelEvent> listener) {
        drawingModelListeners.remove(listener);
    }
    @Override
    public void addListener(InvalidationListener listener) {
        invalidationListeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListeners.remove(listener);
    }

    private void fire(DrawingModelEvent event) {
        for (Listener<DrawingModelEvent> l : drawingModelListeners) {
            l.handle(event);
        }
        for (InvalidationListener l : invalidationListeners) {
            l.invalidated(this);
        }
    }
    private void fireToPropertyListeners(DrawingModelEvent event) {
        for (Listener<DrawingModelEvent> l : propertyListeners) {
            l.handle(event);
        }
    }

    private void fireFigureRemoved(Figure parent, Figure child, int index) {
        fire(new DrawingModelEvent(this, false, parent, child, index));
    }

    private void fireFigureAdded(Figure parent, Figure child, int index) {
        fire(new DrawingModelEvent(this, true, parent, child, index));
    }

    private <T> void firePropertyChange(Figure figure, Key<T> key, T oldValue, T newValue) {
       fireToPropertyListeners(new DrawingModelEvent(this, figure, key, oldValue, newValue));
    }

    private void fireFigureInvalidated(Figure figure) {
        fire(new DrawingModelEvent(this, figure));
    }

    private void handleFigureAdded(Figure figure) {
        handler.addFigure(figure);
        for (Figure child : figure.childrenProperty()) {
            handleFigureAdded(child);
        }
    }

    private void handleFigureRemoved(Figure figure) {
        handler.removeFigure(figure);
        for (Figure child : figure.childrenProperty()) {
            handleFigureRemoved(child);
        }
    }
}
