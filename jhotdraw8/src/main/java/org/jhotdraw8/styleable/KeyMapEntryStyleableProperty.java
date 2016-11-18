/* @(#)KeyMapEntryStyleableProperty.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.styleable;

import javafx.beans.binding.MapExpression;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.ObservableMap;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.KeyMapEntryProperty;

/**
 * KeyMapEntryStyleableProperty.
 *
 * @author Werner Randelshofer
 */
public class KeyMapEntryStyleableProperty<T> extends ObjectPropertyBase<T> implements StyleableProperty<T> {

    private final StyleableMapAccessor<T> key;
    private final CssMetaData<?, T> metaData;
    private final ObservableMap<Key<?>, Object> mapp;
    private final String name;
    private final StyleableMap<Key<?>, Object> map;
    private final Object bean;

    public KeyMapEntryStyleableProperty(ReadOnlyMapProperty<Key<?>, Object> mapp, StyleableMapAccessor<T> key, String name, CssMetaData<?, T> metaData) {
        this(mapp.getBean(), mapp,  key,  name,  metaData);
    }
    public KeyMapEntryStyleableProperty(Object bean,ObservableMap<Key<?>, Object> mapp, StyleableMapAccessor<T> key, String name, CssMetaData<?, T> metaData) {
        @SuppressWarnings("unchecked")
        StyleableMap<Key<?>, Object> m = (StyleableMap<Key<?>, Object>) mapp;
        this.map = m;
        this.key = key;
        this.metaData = metaData;
        this.mapp = mapp;
        this.name = name;
        this.bean=bean;
        bindBidirectional(new KeyMapEntryProperty<T>(mapp, key));
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CssMetaData<?, T> getCssMetaData() {
        return metaData;
    }

    /**
     * This method is called from CSS code to set the value of the property.
     *
     * @param origin the style origin
     * @param value the value null removes the key from the style origin
     */
    @Override
    public void applyStyle(StyleOrigin origin, T value) {
        key.put(map.getMap(origin), value);
    }

    @Override
    public StyleOrigin getStyleOrigin() {
        //ARGH!!! this does not work!!
       return map.getStyleOrigin(key);
    }

}
