/* @(#)SimpleStyleablePropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import org.jhotdraw.collection.Key;

/**
 * SimpleStyleablePropertyBean.
 * @author Werner Randelshofer
 */
public abstract class SimpleStyleablePropertyBean implements StyleablePropertyBean {
    /**
     * Holds the getProperties.
     */
    protected StyleablePropertyMap styleableProperties = new StyleablePropertyMap();
    
    /** Returns the user getProperties. */
    @Override
    public final ReadOnlyMapProperty<Key<?>, Object> propertiesProperty() {
        return styleableProperties.userProperties();
    }

    @Override
    public <T> StyleableProperty<T> getStyleableProperty(Key<T> key) {
        return styleableProperties.getStyleableProperty(key);
    }
    
    /** Returns  the style value. */
    @Override
    public <T> T getStyled(Key<T> key) {
        return key.get(styleableProperties.outputProperties());
    }

    @Override
    public <T> T remove(StyleOrigin origin, Key<T> key) {
        return styleableProperties.remove(origin,key);
    }

    @Override
    public void removeAll(StyleOrigin origin) {
         styleableProperties.removeAll(origin);
    }
    
    
}
