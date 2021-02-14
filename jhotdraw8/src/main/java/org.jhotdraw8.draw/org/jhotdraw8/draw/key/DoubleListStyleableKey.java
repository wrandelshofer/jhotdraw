/*
 * @(#)DoubleListStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.text.CssDoubleConverter;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * DoubleListStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class DoubleListStyleableKey extends AbstractStyleableKey<ImmutableList<Double>> implements WriteableStyleableMapAccessor<ImmutableList<Double>> {

    private static final long serialVersionUID = 1L;

    private Converter<ImmutableList<Double>> converter;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public DoubleListStyleableKey(@NonNull String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *  @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public DoubleListStyleableKey(@NonNull String name, ImmutableList<Double> defaultValue) {
        super(name, new TypeToken<ImmutableList<Double>>() {
        }, defaultValue);

        converter = new CssListConverter<>(new CssDoubleConverter(false));
    }

    @Override
    public @NonNull Converter<ImmutableList<Double>> getCssConverter() {
        return converter;
    }

}
