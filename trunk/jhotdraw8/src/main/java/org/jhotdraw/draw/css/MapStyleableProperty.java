/* @(#)MapStyleableProperty.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import java.util.Map;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import org.jhotdraw.collection.Key;

/**
 * MapStyleableProperty.
 *
 * @author Werner Randelshofer
 */
public class MapStyleableProperty<T> extends ObjectPropertyBase<T> implements StyleableProperty<T> {

    private final Key<T> key;
    private final CssMetaData<?, T> metaData;
    private final ReadOnlyMapProperty<Key<?>, Object> map;

    public MapStyleableProperty(ReadOnlyMapProperty<Key<?>, Object> map,  Key<T> key, CssMetaData<?, T> metaData) {
        this.key = key;
        this.metaData = metaData;
        this.map=map;
        bindBidirectional(new Key.PropertyAt<>(map, key));
    }

    @Override
    public Object getBean() {
        return map.getBean();
    }

    @Override
    public String getName() {
        return key.getName();
    }

    @Override
    public CssMetaData<?, T> getCssMetaData() {
        return metaData;
    }

    /**
     *
     * @param origin the style origin
     * @param value the value null removes the key from the style origin
     */
    @Override
    public void applyStyle(StyleOrigin origin, T value) {
        
                    throw new InternalError("not yet implemented");
        
    }

    @Override
    public StyleOrigin getStyleOrigin() {
                    throw new InternalError("not yet implemented");
       // return StyleablePropertyMap1.this.getStyleOrigin(key);
    }

}
