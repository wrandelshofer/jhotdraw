/*
 * @(#)DrawingModelFigureChildrenObservableList.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.model;

import javafx.collections.ListChangeListener;
import javafx.collections.transformation.TransformationList;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ObservableListProxy;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.event.WeakListener;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * This class allows to provide a proxy for the children list of a figure,
 * the proxy performs all changes on the children list via the DrawingModel.
 */
public class DrawingModelFigureChildrenObservableList extends TransformationList<Figure, Figure> {
    private final @NonNull DrawingModel model;
    private final @NonNull Figure parent;
    private final @NonNull Listener<DrawingModelEvent> drawingModelEventListener;

    /**
     * Creates a new Transformation list wrapped around the source list.
     */
    public DrawingModelFigureChildrenObservableList(@NonNull DrawingModel model, @NonNull Figure parent) {
        super(parent.getChildren());
        this.model = model;
        this.parent = parent;

        drawingModelEventListener = event -> {
            final Figure node = event.getNode();
            if (node != null && node.getParent() == parent) {
                final int index = node.getParent().getChildren().indexOf(node);
                fireChange(new ListChangeListener.Change<Figure>(this) {
                    private boolean invalid = true;

                    @Override
                    public boolean next() {
                        if (invalid) {
                            invalid = false;
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void reset() {
                        invalid = true;
                    }

                    @Override
                    public int getFrom() {
                        return index;
                    }

                    @Override
                    public int getTo() {
                        return index + 1;
                    }

                    @Override
                    public boolean wasUpdated() {
                        return !invalid;
                    }

                    @Override
                    public List<Figure> getRemoved() {
                        return Collections.emptyList();
                    }

                    @Override
                    protected int[] getPermutation() {
                        return new int[0];
                    }
                });
            }
        };
        model.addDrawingModelListener(
                new WeakListener<>(
                        drawingModelEventListener
                        , model::removeDrawingModelListener)
        );
    }

    @Override
    protected void sourceChanged(ListChangeListener.Change<? extends Figure> c) {
        fireChange(new ObservableListProxy.ChangeProxy<>(this, c, Function.identity()));
    }

    @Override
    public int getSourceIndex(int index) {
        return index;
    }

    // XXX supertype method is only available since Java 9
    //@Override
    public int getViewIndex(int index) {
        return index;
    }

    @Override
    public Figure get(int index) {
        return getSource().get(index);
    }

    @Override
    public int size() {
        return getSource().size();
    }

    @Override
    public void add(int index, Figure element) {
        model.insertChildAt(element, parent, index);
    }

    @Override
    public Figure remove(int index) {
        return model.removeFromParent(parent, index);
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Figure) {
            final Figure f = (Figure) o;
            if (f.getParent() != null)
                model.removeFromParent(f);
            return f.getParent() == null;
        }
        return false;
    }

    @Override
    public Figure set(int index, Figure e) {
        final Figure oldChild = remove(index);
        add(index, e);
        return oldChild;
    }
}
