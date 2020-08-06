/*
 * @(#)AbstractFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.SharedKeysMap;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.styleable.AbstractStyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * AbstractFigure.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractFigure extends AbstractStyleablePropertyBean implements Figure, CacheableFigure {

    @NonNull
    private final static Map<Key<?>, Integer> cachedValuesKeyMap = Collections.synchronizedMap(new HashMap<>());

    private transient Map<Key<?>, Object> cachedValues;
    private ObservableSet<Figure> layoutObservers;
    @Nullable
    private Drawing drawing;
    @Nullable
    private final ObjectProperty<Figure> parent = new SimpleObjectProperty<>(this, Figure.PARENT_PROPERTY);
    private CopyOnWriteArrayList<Listener<FigurePropertyChangeEvent>> propertyChangeListeners;
    private volatile Transform cachedLocalToWorld;

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
    protected void doAddNotify(Drawing drawing) {

    }

    /**
     * This method is called by {@link #removeNotify}. The implementation of
     * this class is empty.
     *
     * @param drawing the drawing
     */
    protected void doRemoveNotify(Drawing drawing) {

    }

    @Override
    public <T> T getCachedValue(@NonNull Key<T> key) {
        return (cachedValues == null) ? key.getDefaultValue() : key.get(cachedValues);
    }

    @NonNull
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

    @Nullable
    @Override
    final public Drawing getDrawing() {
        return drawing;
    }

    @Override
    public final ObservableSet<Figure> getLayoutObservers() {
        if (layoutObservers == null) {
            layoutObservers = FXCollections.synchronizedObservableSet(
                    FXCollections.observableSet(new LinkedHashSet<>()));
        }
        return layoutObservers;
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
    @NonNull
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
    public void setPropertyMap(@NonNull HashMap<String, Object> newMap) {
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

    @Override
    public @NonNull ObjectProperty<Figure> parentProperty() {
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

    /**
     * This method fits into {@link org.jhotdraw8.draw.figure.TransformCacheableFigure#getCachedLocalToWorld()} .
     */
    @Nullable
    public Transform getCachedLocalToWorld() {
        return cachedLocalToWorld;
    }

    /**
     * This method fits into {@link org.jhotdraw8.draw.figure.TransformCacheableFigure#setCachedLocalToWorld} .
     */
    public Transform setCachedLocalToWorld(@Nullable Transform newValue) {
        Transform oldValue = this.cachedLocalToWorld;
        this.cachedLocalToWorld = newValue;
        return oldValue;
    }

    @Override
    public <T> T setCachedValue(@NonNull Key<T> key, @Nullable T value) {
        if (cachedValues == null) {
            cachedValues = new SharedKeysMap<>(cachedValuesKeyMap);
        }
        return (value == null) ? key.remove(cachedValues) : key.put(cachedValues, value);
    }

    @Override
    public void updateCss(RenderContext ctx) {
        Drawing d = getDrawing();
        if (d != null) {
            StylesheetsManager<Figure> styleManager = d.getStyleManager();
            if (styleManager != null) {
                styleManager.applyStylesheetsTo(this);
            }
        }
        invalidateTransforms();
    }

    @Override
    protected <T> void changed(Key<T> key, T oldValue, T newValue) {
        firePropertyChangeEvent(this, key, oldValue, newValue);
    }
}
