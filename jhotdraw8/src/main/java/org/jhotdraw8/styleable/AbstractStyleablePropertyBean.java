/* @(#)AbstractStyleablePropertyBean.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;

/**
 * AbstractStyleablePropertyBean.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractStyleablePropertyBean implements StyleablePropertyBean {
    // FIXME should be one instance per class. The instance should be unmodifiable.
    private final static Map<Class<?>, Map<Key<?>, Integer>> keyMaps = new HashMap<>();

    /**
     * Holds the properties.
     */
    // protected StyleablePropertyMap styleableProperties = new StyleablePropertyMap();
    //protected final StyleableMap<Key<?>, Object> properties = new OldStyleableMap<Key<?>, Object>() {
    protected final StyleableMap<Key<?>, Object> properties = new SimpleStyleableMap<Key<?>, Object>(
            keyMaps.computeIfAbsent(getClass(), k -> new HashMap<>())
    ) {

        @Override
        @SuppressWarnings("unchecked")
        protected void callObservers(StyleOrigin origin, MapChangeListener.Change<Key<?>, Object> change) {
            changed((Key<Object>) change.getKey(), change.getValueRemoved(), change.getValueAdded());
            AbstractStyleablePropertyBean.this.callObservers(origin, false, change);
            super.callObservers(origin, change);
        }

    };

    /**
     * Returns the user properties.
     */
    @Nonnull
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

    @Nonnull
    protected StyleableMap<Key<?>, Object> getStyleableMap() {
        @SuppressWarnings("unchecked")
        StyleableMap<Key<?>, Object> map = properties;
        return map;
    }

    /**
     * Returns the style value.
     */
    @Override
    public <T> T getStyled(@Nonnull MapAccessor<T> key) {
        StyleableMap<Key<?>, Object> map = getStyleableMap();
        @SuppressWarnings("unchecked")
        T ret = key.get(map.getStyledMap());// key may invoke get multiple times!
        return ret;
    }

    @Override
    public <T> T getStyled(@Nullable StyleOrigin origin, @Nonnull MapAccessor<T> key) {
        if (origin == null) {
            return getStyled(key);
        }
        Map<Key<?>, Object> map = getStyleableMap().getMap(origin);
        return key.get(map);
    }

    @Override
    public <T> boolean containsKey(StyleOrigin origin, @Nonnull MapAccessor<T> key) {
        return key.containsKey(getStyleableMap().getMap(origin));
    }

    /**
     * Sets the style value.
     */
    @Override
    public <T> T setStyled(StyleOrigin origin, @Nonnull MapAccessor<T> key, T newValue) {
        StyleableMap<Key<?>, Object> map = getStyleableMap();
        @SuppressWarnings("unchecked")
        T ret = key.put(map.getMap(origin), newValue);
        return ret;
    }

    @Override
    public <T> T remove(StyleOrigin origin, @Nonnull MapAccessor<T> key) {
        @SuppressWarnings("unchecked")
        T ret = key.remove(getStyleableMap().getMap(origin));
        return ret;
    }

    @Override
    public void removeAll(StyleOrigin origin) {
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

}
