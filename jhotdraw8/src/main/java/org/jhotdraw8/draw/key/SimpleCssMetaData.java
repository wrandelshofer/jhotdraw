/* @(#)SimpleCssMetaData.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.beans.property.Property;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;

/**
 * SimpleCssMetaData.
 *
 * @author Werner Randelshofer
 */
class SimpleCssMetaData<S extends Styleable, V> extends CssMetaData<S, V> {

    SimpleCssMetaData(
            final String property,
            final Function<S, StyleableProperty<V>> function,
            final StyleConverter<?, V> converter,
            final V initialValue,
            final boolean inherits) {
        super(property, converter, initialValue, inherits);
        this.function = function;
    }

    private final Function<S, StyleableProperty<V>> function;

    public final boolean isSettable(S styleable) {
        final StyleableProperty<V> prop = getStyleableProperty(styleable);
        if (prop instanceof Property) {
            return !((Property) prop).isBound();
        }
        // can't set this property if getStyleableProperty returns null!
        return prop != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final StyleableProperty<V> getStyleableProperty(S styleable) {
        if (styleable != null) {
            StyleableProperty<V> property = function.apply(styleable);
            return property;
        }
        return null;
    }

}
