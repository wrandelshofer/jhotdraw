/*
 * @(#)AbstractCompositeFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import static org.jhotdraw.draw.Figure.CHILDREN_PROPERTY;
import static java.lang.Math.max;
import static java.lang.Math.min;
import javafx.geometry.Point2D;
import org.jhotdraw.draw.connector.Connector;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * This base class can be used to implement figures which support child figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <C> the child type
 * @param <P> the parent type
 */
public abstract class AbstractCompositeFigure extends AbstractFigure {

    private final ReadOnlyListProperty<Figure> children = new ReadOnlyListWrapper<>(this, CHILDREN_PROPERTY, FXCollections.observableList(new ArrayList<Figure>())).getReadOnlyProperty();

    {
        children.addListener(new ListChangeListener<Figure>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends Figure> c) {
                while (c.next()) {
                    final int from = c.getFrom();
                    final int to = c.getTo();
                    final ObservableList<? extends Figure> list = c.getList();
                    if (c.wasPermutated()) {
                    } else if (c.wasUpdated()) {
                    } else {
                        if (c.wasRemoved()) {
                            final List<? extends Figure> removed = c.getRemoved();
                            for (Figure child : removed) {
                                child.parentProperty().set(null);
                            }
                        }
                        if (c.wasAdded()) {
                            for (int i = from; i < to; i++) {
                                Figure f = list.get(i);
                                Figure oldParent = f.getParent();
                                if (oldParent != null) {
                                    oldParent.remove(f);
                                }
                                f.parentProperty().set(AbstractCompositeFigure.this);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public final ReadOnlyListProperty<Figure> childrenProperty() {
        return children;
    }

    @Override
    public final boolean allowsChildren() {
        return true;
    }

    @Override
    public Bounds getBoundsInLocal() {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Figure child : childrenProperty()) {
            Bounds b = child.getBoundsInParent();
            minX = min(minX, b.getMinX());
            maxX = max(maxX, b.getMaxX());
            minY = min(minY, b.getMinY());
            maxY = max(maxY, b.getMaxY());
        }

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        ObservableList<Figure> cs = children();
        for (int i = cs.size() - 1; i >= 0; i--) {
            Figure c = cs.get(i);
            Connector cr = c.findConnector(p, prototype);
            if (cr != null) {
                return cr;
            }
        }
        return null;
    }

    /** First layout all children and then layout self. */
    @Override
    public final void layout() {
        for (Figure child:children()) {
            child.layout();
        }
        doLayout();
    }
    
    /** Layout self. */
    protected void doLayout() {
        
    }
    

}
