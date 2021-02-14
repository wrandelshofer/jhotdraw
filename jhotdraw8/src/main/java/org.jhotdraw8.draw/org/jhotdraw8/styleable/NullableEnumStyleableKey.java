/*
 * @(#)NullableEnumStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.text.Converter;

/**
 * NullableEnumStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class NullableEnumStyleableKey<T extends Enum<T>> extends SimpleStyleableKey<T> implements WriteableStyleableMapAccessor<T> {

    private static final long serialVersionUID = 1L;

    private final @NonNull Converter<T> converter;

    /**
     * Creates a new instance with the specified name, enum class, mask and with
     * null as the default value.
     *
     * @param name  The name of the key.
     * @param clazz The enum class.
     */
    public NullableEnumStyleableKey(String name, Class<T> clazz) {
        this(name, clazz, null);
    }

    /**
     * Creates a new instance with the specified name, enum class, mask and
     * default value.
     *
     * @param name         The name of the key.
     * @param clazz        The enum class.
     * @param defaultValue The default value.
     */
    public NullableEnumStyleableKey(String name, Class<T> clazz, @Nullable T defaultValue) {
        super(name, clazz, null, defaultValue);
        converter = new CssEnumConverter<>(getRawValueType(), true);
    }

    @Override
    public @NonNull Converter<T> getCssConverter() {
        return converter;
    }
}
