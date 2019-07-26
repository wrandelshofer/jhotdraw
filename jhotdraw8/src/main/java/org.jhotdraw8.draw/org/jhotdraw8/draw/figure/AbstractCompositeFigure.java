/*
 * @(#)AbstractCompositeFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.IndexedSet;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.css.CssRectangle2D;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * This base class can be used to implement figures which support child figures.
 *
 * @author Werner Randelshofer
 * @design.pattern Figure Composite, Composite.
 */
public abstract class AbstractCompositeFigure extends AbstractFigure {

    private class ChildList extends IndexedSet<Figure> {

        @Override
        public int indexOf(Object o) {
            if ((o instanceof Figure) && ((Figure) o).getParent() == AbstractCompositeFigure.this) {
                return super.indexOf(o);// linear search!
            }
            return -1;
        }

        @Override
        public boolean contains(Object o) {
            return ((o instanceof Figure) && ((Figure) o).getParent() == AbstractCompositeFigure.this);
        }

        @Override
        protected void onAdded(@Nonnull Figure e) {
            Figure oldParent = e.getParent();
            if (oldParent != null && oldParent != AbstractCompositeFigure.this) {
                oldParent.removeChild(e);
            }
            e.parentProperty().set(AbstractCompositeFigure.this);
        }

        @Override
        protected void onRemoved(@Nonnull Figure e) {
            e.parentProperty().set(null);
        }

        @Override
        protected boolean doAdd(int index, @Nonnull Figure element, boolean checkForDuplicates) {
            Figure oldParent = element.getParent();
            if (oldParent != AbstractCompositeFigure.this) {
                return super.doAdd(index, element, false);
            } else {
                return super.doAdd(index, element, true);// linear search!
            }
        }

    }

    /**
     * The name of the children property.
     */
    public final static String CHILDREN_PROPERTY = "children";

    private final ChildList children = new ChildList();

    /*
    {
        children.addListener(new ListChangeListener<Figure>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends Figure> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        final List<? extends Figure> removedList = c.getRemoved();
                        for (Figure removed : removedList) {
                            removed.parentProperty().set(null);
                        }
                    }
                    if (c.wasAdded()) {
                        final ObservableList<? extends Figure> addedList = c.getList();
                        for (int i = c.getFrom(), to = c.getTo(); i < to; i++) {
                            Figure added = addedList.get(i);
                            Figure oldParentOfAdded = added.getParent();
                            if (oldParentOfAdded != null) {
                                if (oldParentOfAdded == AbstractCompositeFigure.this) {
                                    int lastIndex = children.lastIndexOf(added);
                                    int firstIndex = children.indexOf(added);
                                    if (lastIndex != firstIndex) {
                                        children.removeChild(firstIndex == i ? lastIndex : firstIndex);
                                    }
                                } else {
                                    System.out.println("AbstractCompositeFigure.oldParentOfAdded " + oldParentOfAdded);
                                    oldParentOfAdded.removeChild(added);
                                }
                            }
                            added.parentProperty().set(AbstractCompositeFigure.this);
                        }
                    }
                }
            }
        });
    }*/
    @Nonnull
    @Override
    public ObservableList<Figure> getChildren() {
        return children;
    }

    @Override
    public final boolean isAllowsChildren() {
        return true;
    }

    @Nonnull
    @Override
    public Bounds getBoundsInLocal() {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Figure child : getChildren()) {
            Bounds b = child.getBoundsInParent();
            minX = min(minX, b.getMinX());
            maxX = max(maxX, b.getMaxX());
            minY = min(minY, b.getMinY());
            maxY = max(maxY, b.getMaxY());
        }
        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    @Nonnull
    @Override
    public CssRectangle2D getCssBoundsInLocal() {
        return new CssRectangle2D(getBoundsInLocal());
    }

    @Nonnull
    @Override
    public Bounds getBoundsInParent() {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        Transform t = getLocalToParent();

        for (Figure child : getChildren()) {
            Bounds b = t.transform(child.getBoundsInParent());
            minX = min(minX, b.getMinX());
            maxX = max(maxX, b.getMaxX());
            minY = min(minY, b.getMinY());
            maxY = max(maxY, b.getMaxY());
        }
        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public void firePropertyChangeEvent(@Nonnull FigurePropertyChangeEvent event) {
        final Figure source = event.getSource();
        if (source.getParent() == this) {
            children.fireItemUpdated(children.indexOf(source));
        }
        super.firePropertyChangeEvent(event); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> void firePropertyChangeEvent(@Nullable Figure source, Key<T> key, T oldValue, T newValue) {
        if (children.hasChangeListeners()) {
            children.fireItemUpdated(children.indexOf(source));
        }
        super.firePropertyChangeEvent(source, key, oldValue, newValue); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Replaces the children of this figure with the specified list of children.
     * <p>
     * This method is used for XML serialization using the Java XMLEncoder and
     * XMLDecoder classes.
     *
     * @param newChildren the new children
     */
    public void setChildList(ArrayList<Figure> newChildren) {
        getChildren().setAll(newChildren);
    }

    /**
     * Returns a new list instance with all children of this figure.
     * <p>
     * This method is used for XML serialization using the Java XMLEncoder and
     * XMLDecoder classes.
     *
     * @return a new list instance
     */
    @Nonnull
    public ArrayList<Figure> getChildList() {
        return new ArrayList<>(getChildren());
    }
}
