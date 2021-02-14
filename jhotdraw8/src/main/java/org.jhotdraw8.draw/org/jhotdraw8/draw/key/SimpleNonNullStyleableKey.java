/*
 * @(#)CssSizeStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * CssSizeStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class SimpleNonNullStyleableKey<T> extends AbstractStyleableKey<@NonNull T> implements WriteableStyleableMapAccessor<@NonNull T>,
        NonNullMapAccessor<@NonNull T> {

    static final long serialVersionUID = 1L;

    private final Converter<@NonNull T> converter;


    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public SimpleNonNullStyleableKey(String name, @NonNull TypeToken<T> typeToken, @NonNull T defaultValue, Converter<T> converter) {
        super(name, typeToken, defaultValue);
        this.converter = converter;
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public SimpleNonNullStyleableKey(String name, @NonNull Class<T> clazz, @NonNull T defaultValue, Converter<T> converter) {
        super(name, clazz, defaultValue);
        this.converter = converter;
    }


    @Override
    public @NonNull Converter<T> getCssConverter() {
        return converter;
    }

}
