/*
 * @(#)NullableEnumStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * NullableEnumStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class NullableEnumStyleableKey<T extends Enum<T>> extends AbstractStyleableKey<T> implements WritableStyleableMapAccessor<T> {

    private static final long serialVersionUID = 1L;


    /**
     * Creates a new instance with the specified name, enum class, mask and
     * default value.
     *
     * @param name         The name of the key.
     * @param clazz        The enum class.
     * @param defaultValue The default value.
     */
    public NullableEnumStyleableKey(@NonNull String name, @NonNull Class<T> clazz, @Nullable T defaultValue) {
        super(name, clazz, defaultValue);
        converter = new CssEnumConverter<>(getRawValueType(), true);
    }

    private final @NonNull Converter<T> converter;

    @Override
    public @NonNull Converter<T> getCssConverter() {
        return converter;
    }
}
