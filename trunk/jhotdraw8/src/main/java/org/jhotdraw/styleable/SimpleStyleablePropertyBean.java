/* @(#)SimpleStyleablePropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.styleable;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapPropertyBase;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.ObservableMap;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import org.jhotdraw.collection.Key;

/**
 * SimpleStyleablePropertyBean.
 *
 * @author Werner Randelshofer
 */
public abstract class SimpleStyleablePropertyBean implements StyleablePropertyBean {

    /**
     * Holds the properties.
     */
    // protected StyleablePropertyMap styleableProperties = new StyleablePropertyMap();
    protected final ReadOnlyMapProperty<Key<?>, Object> properties =//
            new ReadOnlyMapWrapper<Key<?>, Object>(this, PROPERTIES_PROPERTY, new StyleableMap<>());

    /**
     * Returns the user getProperties.
     */
    @Override
    public final ReadOnlyMapProperty<Key<?>, Object> propertiesProperty() {
        return properties;
    }

    @Override
    public <T> StyleableProperty<T> getStyleableProperty(Key<T> key) {
        if (key instanceof StyleableKey) {
            StyleableKey<T> skey = (StyleableKey<T>) key;
            return new KeyMapEntryStyleableProperty<T>(properties, skey, skey.getCssName(), skey.getCssMetaData());
        } else {
            return null;
        }
    }

    protected StyleableMap<Key<?>, Object> getStyleableMap() {
        @SuppressWarnings("unchecked")
        StyleableMap<Key<?>, Object> map = (StyleableMap<Key<?>, Object>) properties.get();
        return map;
    }

    /**
     * Returns the style value.
     */
    @Override
    public <T> T getStyled(Key<T> key) {
        StyleableMap<Key<?>, Object> map = getStyleableMap();
        @SuppressWarnings("unchecked")
        T ret = (T) (map.containsStyledKey(key) ? map.getStyled(key) : key.getDefaultValue());
        return ret;
    }
    /**
     * Sets the style value.
     */
    @Override
    public <T> T setStyled(StyleOrigin origin, Key<T> key, T newValue) {
        StyleableMap<Key<?>, Object> map = getStyleableMap();
        @SuppressWarnings("unchecked")
        T ret = (T) map.put(origin, key, newValue);
        return ret;
    }

    @Override
    public <T> T remove(StyleOrigin origin, Key<T> key) {
        @SuppressWarnings("unchecked")
        T ret = (T)  getStyleableMap().remove(origin, key);
        return ret;
    }

    @Override
    public void removeAll(StyleOrigin origin) {
        getStyleableMap().removeAll(origin);
    }

    
}
