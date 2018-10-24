/* @(#)AbstractFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.SharedKeysMap;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.styleable.AbstractStyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * AbstractFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFigure extends AbstractStyleablePropertyBean implements Figure, CacheableFigure {

    @Nonnull
    private static Map<Key<?>, Integer> cachedValuesKeyMap = new HashMap<>();

    private transient Map<Key<?>, Object> cachedValues;
    private ObservableList<Figure> dependentFigures;
    @Nullable
    private Drawing drawing;
    @Nullable
    private final ObjectProperty<Figure> parent = new ObjectPropertyBase<Figure>() {

        @Override
        protected void fireValueChangedEvent() {
            if (get() != null && !isSuitableParent(get())) {
                throw new IllegalArgumentException(get() + " is not a suitable parent for this figure. this="+this);
            }
            super.fireValueChangedEvent();
        }

        @Nonnull
        @Override
        public Object getBean() {
            return AbstractFigure.this;
        }

        @Nonnull
        @Override
        public String getName() {
            return PARENT_PROPERTY;
        }

    };
    private CopyOnWriteArrayList<Listener<FigurePropertyChangeEvent>> propertyChangeListeners;

    /**
     * This method calls {@link #doAddNotify}.
     */
    @Override
    final public void addNotify(Drawing drawing) {
        this.drawing = drawing;
        doAddNotify(drawing);
    }

    /**
     * This method is called by {@link #addNotify}. The implementation of this
     * class is empty.
     *
     * @param drawing the drawing
     */
    protected void doAddNotify( Drawing drawing) {

    }

    /**
     * This method is called by {@link #removeNotify}. The implementation of
     * this class is empty.
     *
     * @param drawing the drawing
     */
    protected void doRemoveNotify( Drawing drawing) {

    }

    @Override
    public <T> T getCachedValue(@Nonnull Key<T> key) {
        return (cachedValues == null) ? key.getDefaultValue() : key.get(cachedValues);
    }

    @Nonnull
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> list = new ArrayList<>();
        for (MapAccessor<?> key : getSupportedKeys()) {
            if (key instanceof WriteableStyleableMapAccessor<?>) {
                WriteableStyleableMapAccessor<?> sk = (WriteableStyleableMapAccessor<?>) key;

                CssMetaData<? extends Styleable, ?> md = sk.getCssMetaData();
                list.add(md);
            }
        }
        return list;
    }

    @javax.annotation.Nullable
    @Override
    final public Drawing getDrawing() {
        return drawing;
    }

    @Override
    public final Collection<Figure> getLayoutObservers() {
        if (dependentFigures == null) {
            dependentFigures = FXCollections.observableArrayList();
        }
        return dependentFigures;
    }

    @Override
    public CopyOnWriteArrayList<Listener<FigurePropertyChangeEvent>> getPropertyChangeListeners() {
        if (propertyChangeListeners == null) {
            propertyChangeListeners = new CopyOnWriteArrayList<>();
        }
        return propertyChangeListeners;
    }

    /**
     * Returns a new map instance with all properties of this figure.
     * <p>
     * This method is used for XML serialization using the Java XMLEncoder and
     * XMLDecoder classes.
     *
     * @return a new list instance
     */
    @Nonnull
    public Map<String, Object> getPropertyMap() {
        HashMap<String, Object> result = new HashMap<>();
        for (Map.Entry<Key<?>, Object> e : getProperties().entrySet()) {
            Key<?> k = e.getKey();
            if (!Objects.equals(e.getValue(), k.getDefaultValue())) {
                result.put(k.getName(), e.getValue());
            }
        }
        return result;
    }

    /**
     * Replaces the properties map of this figure with the contents of the
     * specified map.
     * <p>
     * This method is used for XML serialization using the Java XMLEncoder and
     * XMLDecoder classes.
     *
     * @param newMap the new properties
     */
    public void setPropertyMap(@Nonnull HashMap<String, Object> newMap) {
        HashMap<String, Key<?>> keyst = new HashMap<>();
        Map<Key<?>, Object> m = getProperties();
        for (MapAccessor<?> ma : Figure.getDeclaredAndInheritedMapAccessors(getClass())) {
            if (ma instanceof Key<?>) {
                keyst.put(ma.getName(), (Key<?>) ma);
            }
        }
        for (Map.Entry<String, Object> e : newMap.entrySet()) {
            String name = e.getKey();
            Key<?> key = keyst.get(name);
            if (key != null) {
                m.put(key, e.getValue());
            }
        }
    }

    @Override
    public boolean hasPropertyChangeListeners() {
        return propertyChangeListeners != null && !propertyChangeListeners.isEmpty();
    }

    /**
     * This method whether the provided figure is a suitable parent for this
     * figure.
     * <p>
     * This implementation returns false if {@code newParent} is a
     * {@link Drawing}. Because only {@link org.jhotdraw8.draw.figure.Layer}s
     * may have {@code org.jhotdraw8.draw.Drawing} as a parent.
     *
     * @param newParent The new parent figure.
     * @return true if {@code newParent} is an acceptable parent
     */
    @Override
    public boolean isSuitableParent(@javax.annotation.Nullable Figure newParent) {
        return newParent != null && !(newParent instanceof Drawing);
    }

    @javax.annotation.Nullable
    @Override
    public ObjectProperty<Figure> parentProperty() {
        return parent;
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void removeAllLayoutSubjects() {
        // empty
    }

    /**
     * This implementation is empty.
     *
     * @param connectedFigure the connected figure
     */
    @Override
    public void removeLayoutSubject(Figure connectedFigure) {
        // empty
    }

    /**
     * This method calls {@link #doAddNotify}.
     */
    @Override
    final public void removeNotify(Drawing drawing) {
        this.drawing = null;
        doRemoveNotify(drawing);
    }

    @Override
    public <T> T setCachedValue(@Nonnull Key<T> key, @javax.annotation.Nullable T value) {
        if (cachedValues == null) {
            cachedValues = new SharedKeysMap<>(cachedValuesKeyMap);
        }
        return (value == null) ? key.remove(cachedValues) : key.put(cachedValues, value);
    }

    @Override
    public void updateCss() {
        getStyleableMap().clearAuthorAndInlineValues();
        Drawing d = getDrawing();
        if (d != null) {
            StylesheetsManager<Figure> styleManager = d.getStyleManager();
            styleManager.applyStylesheetsTo(this);
        }
        invalidateTransforms();
    }

}
