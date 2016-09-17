/* @(#)AbstractFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.FigureKey;
import org.jhotdraw.event.Listener;
import org.jhotdraw.styleable.SimpleStyleablePropertyBean;
import org.jhotdraw.styleable.StyleableMapAccessor;
import org.jhotdraw.css.StylesheetsManager;

/**
 * AbstractFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFigure extends SimpleStyleablePropertyBean implements Figure {

    private final ObjectProperty<Figure> parent = new SimpleObjectProperty<Figure>(this, PARENT_PROPERTY) {

        @Override
        protected void fireValueChangedEvent() {
            if (get() != null && !isSuitableParent(get())) {
                throw new IllegalArgumentException(get() + " is not a suitable parent for this figure.");
            }
            super.fireValueChangedEvent();
        }

    };
    private ObservableSet<Figure> dependentFigures;
    private CopyOnWriteArrayList<Listener<FigurePropertyChangeEvent>> propertyChangeListeners;

    @Override
    public final ObservableSet<Figure> getDependentFigures() {
        if (dependentFigures == null) {
            dependentFigures = FXCollections.observableSet(Collections.newSetFromMap(new IdentityHashMap<Figure, Boolean>()));
        }
        return dependentFigures;
    }

    @Override
    public ObjectProperty<Figure> parentProperty() {
        return parent;
    }

    /**
     * This method whether the provided figure is a suitable parent for this
     * figure.
     * <p>
     * This implementation returns false if {@code newParent} is a
     * {@link Drawing}. Because only {@link org.jhotdraw.draw.Layer}s may have
     * {@code org.jhotdraw.draw.Drawing} as a parent.
     *
     * @param newParent The new parent figure.
     * @return true if {@code newParent} is an acceptable parent
     */
    @Override
    public boolean isSuitableParent(Figure newParent) {
        return newParent != null && !(newParent instanceof Drawing);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> list = new ArrayList<>();
        for (MapAccessor<?> key : getSupportedKeys()) {
            if (key instanceof StyleableMapAccessor<?>) {
                StyleableMapAccessor<?> sk = (StyleableMapAccessor<?>) key;

                CssMetaData<? extends Styleable, ?> md = sk.getCssMetaData();
                list.add(md);
            }
        }
        return list;
    }

    @Override
    public void updateCss() {
        getStyleableMap().clearNonUserValues();
        Drawing d = getDrawing();
        if (d != null) {
            StylesheetsManager<Figure> styleManager = d.getStyleManager();
            styleManager.applyStylesheetsTo(this);
            for (Figure child : getChildren()) {
                child.updateCss();// should not recurse, because style manager knows better if it is worthwile?
            }
        }
        invalidateTransforms();
    }

    @Override
    public String toString() {
        String className = getClass().getName();
        className = className.substring(className.lastIndexOf('.') + 1);
        StringBuilder buf = new StringBuilder();
        buf.append(className).append('@')//
                .append(Integer.toHexString(hashCode()))//
                .append('{');//
        boolean isFirst = true;
        for (Map.Entry<Key<?>, Object> e : getProperties().entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                buf.append(',');
            }
            buf.append(e.getKey());
            buf.append('=');
            if (e.getValue() instanceof Figure) {
                Figure f = (Figure) e.getValue();
                className = f.getClass().getName();
                className = className.substring(className.lastIndexOf('.') + 1);
                buf.append(className).append('@').append(f.hashCode());
            } else {
                buf.append(e.getValue());
            }
        }
        if (dependentFigures != null) {
            buf.append(", connections={");
            isFirst = true;
            for (Figure f : dependentFigures) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    buf.append(',');
                }
                className = f.getClass().getName();
                className = className.substring(className.lastIndexOf('.') + 1);
                buf.append(className).append('@').append(f.hashCode());
            }
            buf.append("}}");
        }
        return buf.toString();
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void removeConnectionTarget(Figure connectedFigure) {
        // empty
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void removeAllConnectionTargets() {
        // empty
    }

    /**
     * This implementation is empty.
     *
     * @param drawing the drawing to which this figure has been added
     */
    @Override
    public void removeNotify(Drawing drawing) {
    }

    /**
     * This implementation is empty.
     *
     * @param drawing the drawing from which this figure has been removed
     */
    @Override
    public void addNotify(Drawing drawing) {
    }

    @Override
    public CopyOnWriteArrayList<Listener<FigurePropertyChangeEvent>> getPropertyChangeListeners() {
        if (propertyChangeListeners == null) {
            propertyChangeListeners = new CopyOnWriteArrayList<>();
        }
        return propertyChangeListeners;
    }

    @Override
    public boolean hasPropertyChangeListeners() {
        return propertyChangeListeners != null && !propertyChangeListeners.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void callObservers(StyleOrigin origin, boolean willChange, MapChangeListener.Change<Key<?>, Object> change) {
        if (origin == StyleOrigin.USER && !Objects.equals(change.getValueRemoved(), change.getValueAdded())) {
            if (willChange) {
                if (change.getKey() instanceof FigureKey) {
                    if (((FigureKey<?>) change.getKey()).getDirtyMask().containsOneOf(DirtyBits.DEPENDENCY)) {
                        firePropertyChangeEvent(this, FigurePropertyChangeEvent.EventType.WILL_CHANGE, (Key<Object>) change.getKey(), change.getValueRemoved(), change.getValueAdded());
                    }
                }
            } else {
                firePropertyChangeEvent(this, FigurePropertyChangeEvent.EventType.CHANGED, (Key<Object>) change.getKey(), change.getValueRemoved(), change.getValueAdded());
            }
        }
    }
}
