/* @(#)ConnectionsAndLayoutDrawingModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.model;

import java.util.Set;
import javafx.scene.transform.Transform;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.key.FigureKey;

/**
 * This drawing model assumes that the drawing contains figures which perform
 * layouts and has getConnectedFigures between figures.
 * <p>
 * Assumes that a figure which has getConnectedFigures to other figures may have
 * in turn getConnectedFigures from other figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ConnectionsAndLayoutDrawingModel extends AbstractDrawingModel {

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
                fire(DrawingModelEvent.figureRemovedFromParent(this, parent, child, index));
                fire(DrawingModelEvent.nodeInvalidated(this, parent));
            }
        }
        Drawing newDrawing = child.getDrawing();
        if (oldDrawing != newDrawing) {
            if (oldDrawing != null) {
                fire(DrawingModelEvent.figureRemovedFromDrawing(this, oldDrawing, child));
            }
            if (newDrawing != null) {
                fire(DrawingModelEvent.figureAddedToDrawing(this, newDrawing, child));
            }
        }
    }

    @Override
    public void disconnect(Figure figure) {
        for (Figure connectedFigure : figure.getConnectedFigures()) {
            fire(DrawingModelEvent.layoutInvalidated(this, connectedFigure));

        }
        fireLayoutInvalidatedConnectedFigures(figure);
        figure.disconnect();
        fire(DrawingModelEvent.nodeInvalidated(this, figure));
        fire(DrawingModelEvent.layoutInvalidated(this, figure));
    }

    @Override
    public void insertChildAt(Figure child, Figure parent, int index) {
        Drawing oldDrawing = child.getDrawing();
        if (child.getParent() != null) {
            child.getParent().remove(child);
        }
        parent.getChildren().add(index, child);
        fire(DrawingModelEvent.figureAddedToParent(this, parent, child, index));
        fire(DrawingModelEvent.nodeInvalidated(this, parent));
        Drawing newDrawing = child.getDrawing();
        if (oldDrawing != newDrawing) {
            if (oldDrawing != null) {
                fire(DrawingModelEvent.figureRemovedFromDrawing(this, oldDrawing, child));
            }
            if (newDrawing != null) {
                fire(DrawingModelEvent.figureAddedToDrawing(this, newDrawing, child));
            }
        }
    }

    @Override
    public <T> T set(Figure figure, Key<T> key, T newValue) {
        Set<Figure> connectionChange = null;
        if (key instanceof FigureKey) {
            FigureKey<T> fk = (FigureKey<T>) key;
            DirtyMask dm = fk.getDirtyMask();
            if (dm.containsOneOf(DirtyBits.CONNECTION)) {
                connectionChange = figure.getConnectionTargets();
            }

        }

        T oldValue = figure.set(key, newValue);
        if (oldValue != newValue) {
            if (key instanceof FigureKey) {
                FigureKey<T> fk = (FigureKey<T>) key;
                DirtyMask dm = fk.getDirtyMask();
                if (dm.containsOneOf(DirtyBits.NODE)) {
                    fire(DrawingModelEvent.nodeInvalidated(this, figure));
                }
                if (dm.containsOneOf(DirtyBits.LAYOUT)) {
                    fire(DrawingModelEvent.layoutInvalidated(this, figure));
                }
                if (dm.containsOneOf(DirtyBits.CONNECTION_LAYOUT)) {
                    fireLayoutInvalidatedConnectedFigures(figure);
                }
                if (dm.containsOneOf(DirtyBits.TRANSFORM)) {
                    fire(DrawingModelEvent.transformChanged(this, figure));
                }
                if (dm.containsOneOf(DirtyBits.CONNECTION)) {
                    fire(DrawingModelEvent.connectionChanged(this, figure));
                    Set<Figure> connectionsAfter = figure.getConnectionTargets();
                    connectionChange.addAll(connectionsAfter);
                    for (Figure f : connectionChange) {
                        fire(DrawingModelEvent.connectionChanged(this, f));
                        fire(DrawingModelEvent.nodeInvalidated(this, f));
                    }
                }
            }
        }
        return oldValue;
    }

    @Override
    public void reshape(Figure figure, Transform transform) {
        figure.reshape(transform);
        fire(DrawingModelEvent.transformChanged(this, figure));
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, figure));
        fire(DrawingModelEvent.layoutInvalidated(this, figure));
        fireLayoutInvalidatedForFiguresConnectedWithSubtree(figure);
    }

    @Override
    public void reshape(Figure figure, double x, double y, double width, double height) {
        figure.reshape(x, y, width, height);
        fire(DrawingModelEvent.transformChanged(this, figure));
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, figure));
        fire(DrawingModelEvent.layoutInvalidated(this, figure));
        fireLayoutInvalidatedForFiguresConnectedWithSubtree(figure);
    }

    @Override
    public void layout(Figure figure) {
        figure.layout();
        fire(DrawingModelEvent.transformChanged(this, figure));
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, figure));
        fireLayoutInvalidatedForFiguresConnectedWithSubtree(figure);
    }

    @Override
    public void applyCss(Figure figure) {
        figure.applyCss();
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, figure));
        fireLayoutInvalidatedForFiguresConnectedWithSubtree(figure);
    }
}
