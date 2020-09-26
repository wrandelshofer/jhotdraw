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
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssDefaultableValue;
import org.jhotdraw8.css.CssDefaulting;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.styleable.AbstractStyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * AbstractFigure.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractFigure extends AbstractStyleablePropertyBean
        implements Figure, TransformCachingFigure {

    private ObservableSet<Figure> layoutObservers;
    @Nullable
    private Drawing drawing;
    @Nullable
    private final ObjectProperty<Figure> parent = new SimpleObjectProperty<>(this, Figure.PARENT_PROPERTY);
    private CopyOnWriteArrayList<Listener<FigurePropertyChangeEvent>> propertyChangeListeners;
    private Transform cachedLocalToWorld;
    private Transform cachedWorldToParent;
    private Transform cachedParentToWorld;
    private Transform cachedParentToLocal;
    private Transform cachedLocalToParent;
    private Transform cachedWorldToLocal;


    @NonNull
    protected Map<Key<?>, Integer> createKeyMap() {
        return keyMaps.computeIfAbsent(getClass(), k -> {
            int index = 0;

            Set<MapAccessor<?>> accessors = Figure.getDeclaredAndInheritedMapAccessors(getClass());
            IdentityHashMap<Key<?>, Integer> m = new IdentityHashMap<>(accessors.size());

            for (MapAccessor<?> accessor : accessors) {
                if (accessor instanceof Key<?>) {
                    m.put((Key<?>) accessor, index++);
                }
            }
            return m;
        });
    }

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
            layoutObservers =
                    FXCollections.observableSet(new LinkedHashSet<>());
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

    @Override
    @Nullable
    public Transform getCachedLocalToWorld() {
        return cachedLocalToWorld;
    }

    @Override
    public void setCachedLocalToWorld(@Nullable Transform newValue) {
        this.cachedLocalToWorld = newValue;
    }

    @Override
    @Nullable
    public Transform getCachedWorldToParent() {
        return cachedWorldToParent;
    }

    @Override
    public void setCachedWorldToParent(@Nullable Transform newValue) {
        this.cachedWorldToParent = newValue;
    }

    @Override
    @Nullable
    public Transform getCachedParentToLocal() {
        return cachedParentToLocal;
    }

    @Override
    public void setCachedParentToLocal(@Nullable Transform newValue) {
        this.cachedParentToLocal = newValue;
    }

    @Override
    @Nullable
    public Transform getCachedLocalToParent() {
        return cachedLocalToParent;
    }

    @Override
    public void setCachedLocalToParent(@Nullable Transform newValue) {
        this.cachedLocalToParent = newValue;
    }

    @Override
    @Nullable
    public Transform getCachedWorldToLocal() {
        return cachedWorldToLocal;
    }

    @Override
    public void setCachedWorldToLocal(@Nullable Transform newValue) {
        this.cachedWorldToLocal = newValue;
    }

    @Override
    @Nullable
    public Transform getCachedParentToWorld() {
        return cachedParentToWorld;
    }

    @Override
    public void setCachedParentToWorld(@Nullable Transform newValue) {
        this.cachedParentToWorld = newValue;
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
        invalidateTransforms();
        firePropertyChangeEvent(this, key, oldValue, newValue);
    }

    @Override
    public <T> @NonNull T getStyledNonNull(@NonNull NonNullMapAccessor<T> key) {
        T value = super.getStyledNonNull(key);
        if (value instanceof CssDefaultableValue<?>) {
            @SuppressWarnings("unchecked") CssDefaultableValue<T> dv = (CssDefaultableValue<T>) value;
            if (dv.getDefaulting() == CssDefaulting.INHERIT && getParent() != null) {
                value = getParent().getStyledNonNull(key);
            }
        }

        return value;
    }

    @Override
    public <T> @Nullable T getStyled(@NonNull MapAccessor<T> key) {
        T value = super.getStyled(key);

        // FIXME "inherit" is quirky - so we cannot implement it this way
        if (value instanceof CssDefaultableValue<?>) {
            @SuppressWarnings("unchecked") CssDefaultableValue<T> dv = (CssDefaultableValue<T>) value;
            if (dv.getDefaulting() == CssDefaulting.INHERIT && getParent() != null) {
                value = getParent().getStyled(key);
            }
        }
        return value;
    }
}
