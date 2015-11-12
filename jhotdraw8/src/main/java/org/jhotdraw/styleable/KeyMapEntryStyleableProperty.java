/* @(#)KeyMapEntryStyleableProperty.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.styleable;

import java.util.Map;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import org.jhotdraw.beans.MapEntryProperty;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.KeyMapEntryProperty;
import org.jhotdraw.styleable.StyleableMap;

/**
 * KeyMapEntryStyleableProperty.
 *
 * @author Werner Randelshofer
 */
public class KeyMapEntryStyleableProperty<T> extends ObjectPropertyBase<T> implements StyleableProperty<T> {

    private final Key<T> key;
    private final CssMetaData<?, T> metaData;
    private final ReadOnlyMapProperty<Key<?>, Object> mapp;
    private final String name;
    private final StyleableMap<Key<?>, Object> map;

    public KeyMapEntryStyleableProperty(ReadOnlyMapProperty<Key<?>, Object> mapp, Key<T> key, String name, CssMetaData<?, T> metaData) {
        @SuppressWarnings("unchecked")
        StyleableMap<Key<?>, Object> m = (StyleableMap<Key<?>, Object>) mapp.get();
        this.map = m;
        this.key = key;
        this.metaData = metaData;
        this.mapp = mapp;
        this.name = name;
        bindBidirectional(new KeyMapEntryProperty<T>(mapp, key));
    }

    @Override
    public Object getBean() {
        return mapp.getBean();
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
        map.put(origin,key, value);
    }

    @Override
    public StyleOrigin getStyleOrigin() {
       return map.getStyleOrigin(key);
    }

}
