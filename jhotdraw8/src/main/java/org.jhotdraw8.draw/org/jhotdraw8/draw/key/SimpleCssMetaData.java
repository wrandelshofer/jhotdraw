/*
 * @(#)SimpleCssMetaData.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.beans.property.Property;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.Nullable;

import java.util.function.Function;

/**
 * SimpleCssMetaData.
 *
 * @param <S> the type of the styleable object that can be styled with this metadata
 * @param <V> the type of the property value
 * @author Werner Randelshofer
 */
public class SimpleCssMetaData<S extends Styleable, V> extends CssMetaData<S, V> {
    /**
     * Construct a CssMetaData with the given parameters and no sub-properties.
     *
     * @param property     the CSS property
     * @param converter    the StyleConverter used to convert the CSS parsed value to a Java object.
     * @param initialValue The initial or default value of the corresponding StyleableProperty
     * @param inherits     true if this property uses CSS inheritance
     * @param function     the function that converts a value of type S to a Java object
     */
    public SimpleCssMetaData(
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
    @Nullable
    @Override
    public final StyleableProperty<V> getStyleableProperty(@Nullable S styleable) {
        if (styleable != null) {
            StyleableProperty<V> property = function.apply(styleable);
            return property;
        }
        return null;
    }

}
