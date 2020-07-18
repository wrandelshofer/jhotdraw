/*
 * @(#)AbstractStyleablePropertyBean.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AbstractStyleablePropertyBean.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractStyleablePropertyBean implements StyleablePropertyBean {
    private final static Map<Class<?>, Map<Key<?>, Integer>> keyMaps = new ConcurrentHashMap<>();

    /**
     * Holds the properties.
     */
    protected final StyleableMap<Key<?>, Object> properties = new SimpleStyleableMap<Key<?>, Object>(
            keyMaps.computeIfAbsent(getClass(), k -> {
                ConcurrentHashMap<Key<?>, Integer> m = new ConcurrentHashMap<Key<?>, Integer>() {
                    @NonNull
                    final AtomicInteger count = new AtomicInteger();

                    @Override
                    public Integer get(Object key) {
                        return super.computeIfAbsent((Key<?>) key, k -> count.incrementAndGet());
                    }
                };

                return m;
            })
    ) {

        @Override
        @SuppressWarnings("unchecked")
        protected void callObservers(StyleOrigin origin, @NonNull MapChangeListener.Change<Key<?>, Object> change) {
            changed((Key<Object>) change.getKey(), change.getValueRemoved(), change.getValueAdded());
            AbstractStyleablePropertyBean.this.callObservers(origin, false, change);
            super.callObservers(origin, change);
        }

    };

    /**
     * Returns the user properties.
     */
    @NonNull
    @Override
    public final ObservableMap<Key<?>, Object> getProperties() {
        return properties;
    }

    @Nullable
    @Override
    public <T> StyleableProperty<T> getStyleableProperty(MapAccessor<T> key) {
        if (key instanceof WriteableStyleableMapAccessor) {
            WriteableStyleableMapAccessor<T> skey = (WriteableStyleableMapAccessor<T>) key;
            return new KeyMapEntryStyleableProperty<>(this, properties, skey, skey.getCssName(), skey.getCssMetaData());
        } else {
            return null;
        }
    }

    @NonNull
    protected StyleableMap<Key<?>, Object> getStyleableMap() {
        return properties;
    }

    /**
     * Returns the style value.
     */
    @Nullable
    @Override
    public <T> T getStyled(@NonNull MapAccessor<T> key) {
        StyleableMap<Key<?>, Object> map = getStyleableMap();
        @SuppressWarnings("unchecked")
        T ret = key.get(map.getStyledMap());// key may invoke get multiple times!
        return ret;
    }

    @Override
    public <T> T getStyled(@Nullable StyleOrigin origin, @NonNull MapAccessor<T> key) {
        if (origin == null) {
            return getStyled(key);
        }
        Map<Key<?>, Object> map = getStyleableMap().getMap(origin);
        return key.get(map);
    }

    @Override
    public <T> boolean containsKey(@NonNull StyleOrigin origin, @NonNull MapAccessor<T> key) {
        return key.containsKey(getStyleableMap().getMap(origin));
    }

    /**
     * Sets the style value.
     */
    @Nullable
    @Override
    public <T> T setStyled(@NonNull StyleOrigin origin, @NonNull MapAccessor<T> key, T newValue) {
        StyleableMap<Key<?>, Object> map = getStyleableMap();
        @SuppressWarnings("unchecked")
        T ret = key.put(map.getMap(origin), newValue);
        return ret;
    }

    @Nullable
    @Override
    public <T> T remove(@NonNull StyleOrigin origin, @NonNull MapAccessor<T> key) {
        @SuppressWarnings("unchecked")
        T ret = key.remove(getStyleableMap().getMap(origin));
        return ret;
    }

    @Override
    public void removeAll(@NonNull StyleOrigin origin) {
        getStyleableMap().removeAll(origin);
    }

    /**
     * This method is invoked just before listeners are notified. This
     * implementation is empty.
     *
     * @param <T>      the type
     * @param key      the changed key
     * @param oldValue the old value
     * @param newValue the new value
     */
    protected <T> void changed(Key<T> key, T oldValue, T newValue) {
    }

    /**
     * This method is invoked just before listeners are notified. This
     * implementation is empty.
     *
     * @param origin     the style origin
     * @param willChange true if the change is about to be performed, false if
     *                   the change happened
     * @param change     the change
     */
    protected void callObservers(StyleOrigin origin, boolean willChange, MapChangeListener.Change<Key<?>, Object> change) {

    }

    @Override
    public void resetStyledValues() {
        properties.resetStyledValues();
    }
}
