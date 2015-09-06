/*
 * @(#)AbstractCompositeFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import static org.jhotdraw.draw.Figure.CHILDREN_PROPERTY;

/**
 * This base class can be used to implement figures which support child figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
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
                        fireInvalidated();
                    } else if (c.wasUpdated()) {
                        fireInvalidated();
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
                                Figure oldParent = f.parentProperty().get();
                                if (oldParent != null) {
                                    oldParent.remove(f);
                                }
                                f.parentProperty().set(AbstractCompositeFigure.this);
                            }
                        }
                        fireInvalidated();
                    }
                }
            }
        });
    }

    @Override
    public ReadOnlyListProperty<Figure> childrenProperty() {
        return children;
    }

    /**
     * Whether children may be added to this figure.
     */
    @Override
    public boolean allowsChildren() {
        return true;
    }

}
