/* @(#)SimpleCssMetaData.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.beans.property.Property;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javax.annotation.Nullable;

/**
 * SimpleCssMetaData.
 *
 * @author Werner Randelshofer
 * @param <S> the type of the styleable object that can be styled with this metadata 
 * @param <V> the type of the property value
 */
public class SimpleCssMetaData<S extends Styleable, V> extends CssMetaData<S, V> {

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
