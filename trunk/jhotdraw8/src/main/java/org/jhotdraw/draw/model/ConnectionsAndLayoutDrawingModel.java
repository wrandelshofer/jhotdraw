/* @(#)ConnectionsAndLayoutDrawingModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.model;

import java.util.Objects;
import java.util.Set;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.event.Listener;
import org.jhotdraw.draw.key.FigureKey;

/**
 * This drawing model assumes that the drawing contains figures which perform
 * layouts and has getDependentFigures between figures.
 * <p>
 * Assumes that a figure which has getDependentFigures to other figures may have
 * in turn getDependentFigures from other figures.
 *
 * @author Werner Randelshofer
 * @version $Id: ConnectionsAndLayoutDrawingModel.java 1120 2016-01-15 17:37:49Z
 * rawcoder $
 */
public class ConnectionsAndLayoutDrawingModel extends AbstractDrawingModelOLD {

    @Override
    public void setRoot(Drawing root) {
        this.root = root;
        fire(DrawingModelEvent.rootChanged(this, root));
    }

    @Override
    public void removeFromParent(Figure child) {
        Drawing oldDrawing = child.getDrawing();
        Figure parent = child.getParent();
        if (parent != null) {
            int index = parent.getChildren().indexOf(child);
            if (index != -1) {
                parent.getChildren().remove(index);
                fire(DrawingModelEvent.figureRemovedFromParent((DrawingModel) this, parent, child, index));
                fire(DrawingModelEvent.nodeInvalidated((DrawingModel) this, parent));
            }
        }
        Drawing newDrawing = child.getDrawing();
        if (oldDrawing != newDrawing) {
            if (oldDrawing != null) {
                fire(DrawingModelEvent.figureRemovedFromDrawing((DrawingModel) this, oldDrawing, child));
            }
            if (newDrawing != null) {
                fire(DrawingModelEvent.figureAddedToDrawing((DrawingModel) this, newDrawing, child));
            }
        }
    }

    @Override
    public void disconnect(Figure figure) {
        for (Figure connectedFigure : figure.getDependentFigures()) {
            fire(DrawingModelEvent.layoutChanged((DrawingModel) this, connectedFigure));

        }
        fireLayoutInvalidatedConnectedFigures(figure);
        figure.disconnect();
        fire(DrawingModelEvent.nodeInvalidated((DrawingModel) this, figure));
        fire(DrawingModelEvent.layoutChanged((DrawingModel) this, figure));
    }

    @Override
    public void insertChildAt(Figure child, Figure parent, int index) {
        Drawing oldDrawing = child.getDrawing();
        Figure oldParent = child.getParent();
        if (oldParent != null) {
            int oldChildIndex = oldParent.getChildren().indexOf(child);
            oldParent.remove(child);
            fire(DrawingModelEvent.figureRemovedFromParent((DrawingModel) this, oldParent, child, oldChildIndex));
            fire(DrawingModelEvent.nodeInvalidated((DrawingModel) this, oldParent));
        }
        parent.getChildren().add(index, child);
        Drawing newDrawing = child.getDrawing();
        if (oldDrawing != newDrawing) {
            if (oldDrawing != null) {
                fire(DrawingModelEvent.figureRemovedFromDrawing((DrawingModel) this, oldDrawing, child));
            }
            if (newDrawing != null) {
                fire(DrawingModelEvent.figureAddedToDrawing((DrawingModel) this, newDrawing, child));
            }
        }
        fire(DrawingModelEvent.figureAddedToParent((DrawingModel) this, parent, child, index));
        fire(DrawingModelEvent.nodeInvalidated((DrawingModel) this, parent));
    }

    @Override
    public <T> T set(Figure figure, MapAccessor<T> key, T newValue) {
        Set<Figure> connectionChange = null;
        figure.getProperties().addListener(figurePropertyChangeHandler);
        figurePropertyChangeHandler.setFigure(figure);
        T oldValue = figure.set(key, newValue);
        figure.getProperties().removeListener(figurePropertyChangeHandler);
        if (!Objects.equals(oldValue, newValue)) {
            final DirtyMask dm;
            if (key instanceof FigureKey) {
                FigureKey<T> fk = (FigureKey<T>) key;
                dm = fk.getDirtyMask();
            } else {
                dm = DirtyMask.EMPTY;
            }

            if (dm.containsOneOf(DirtyBits.DEPENDENCY)) {
                connectionChange = figure.getProvidingFigures();
            }
            if (dm.containsOneOf(DirtyBits.NODE)) {
                fire(DrawingModelEvent.nodeInvalidated((DrawingModel) this, figure));
            }
            if (dm.containsOneOf(DirtyBits.LAYOUT)) {
                fire(DrawingModelEvent.layoutChanged((DrawingModel) this, figure));
                fireLayoutInvalidatedConnectedFigures(figure);
            }
            if (dm.containsOneOf(DirtyBits.TRANSFORM)) {
                fire(DrawingModelEvent.transformChanged((DrawingModel) this, figure));
            }
            if (dm.containsOneOf(DirtyBits.STYLE)) {
                fire(DrawingModelEvent.styleInvalidated((DrawingModel) this, figure));
            }
            if (dm.containsOneOf(DirtyBits.DEPENDENCY)) {
                fire(DrawingModelEvent.dependencyChanged((DrawingModel) this, figure));
                Set<Figure> connectionsAfter = figure.getProvidingFigures();
                connectionChange.addAll(connectionsAfter);
                for (Figure f : connectionChange) {
                    fire(DrawingModelEvent.dependencyChanged((DrawingModel) this, f));
                    fire(DrawingModelEvent.nodeInvalidated((DrawingModel) this, f));
                }
            }
        }

        return oldValue;
    }

    @Override
    public void reshape(Figure figure, Transform transform) {
        // FIXME this is not sufficient for capturing property changes
        //       we need to listen on the entire subtree!
        figure.getProperties().addListener(figurePropertyChangeHandler);
        figurePropertyChangeHandler.setFigure(figure);
        figure.reshape(transform);
        figure.getProperties().removeListener(figurePropertyChangeHandler);
        fire(DrawingModelEvent.transformChanged((DrawingModel) this, figure));
        fire(DrawingModelEvent.subtreeNodesInvalidated((DrawingModel) this, figure));
        fire(DrawingModelEvent.layoutChanged((DrawingModel) this, figure));
        fireLayoutInvalidatedForFiguresConnectedWithSubtree(figure);
    }

    @Override
    public void reshape(Figure figure, double x, double y, double width, double height) {
        figure.getProperties().addListener(figurePropertyChangeHandler);
        figurePropertyChangeHandler.setFigure(figure);
        figure.reshape(x, y, width, height);
        figure.getProperties().removeListener(figurePropertyChangeHandler);
        fire(DrawingModelEvent.transformChanged((DrawingModel) this, figure));
        fire(DrawingModelEvent.subtreeNodesInvalidated((DrawingModel) this, figure));
        fire(DrawingModelEvent.layoutChanged((DrawingModel) this, figure));
        fireLayoutInvalidatedForFiguresConnectedWithSubtree(figure);
    }

    @Override
    public void layout(Figure figure) {
        figure.layout();
        fire(DrawingModelEvent.transformChanged((DrawingModel) this, figure));
        fire(DrawingModelEvent.subtreeNodesInvalidated((DrawingModel) this, figure));
        fireLayoutInvalidatedForFiguresConnectedWithSubtree(figure);
    }

    @Override
    public void applyCss(Figure figure) {
        figure.updateCss();
        fire(DrawingModelEvent.subtreeNodesInvalidated((DrawingModel) this, figure));
        fireLayoutInvalidatedForFiguresConnectedWithSubtree(figure);
    }

    public ObservableList<Listener<DrawingModelEvent>> getDrawingModelListeners() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ObservableList<InvalidationListener> getInvalidationListeners() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ObjectProperty<Drawing> rootProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
