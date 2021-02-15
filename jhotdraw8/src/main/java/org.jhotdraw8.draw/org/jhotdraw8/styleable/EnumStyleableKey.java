/*
 * @(#)EnumStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.text.Converter;

/**
 * EnumStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class EnumStyleableKey<@Nullable T extends Enum<T>> extends SimpleStyleableKey<@Nullable T>
        implements WritableStyleableMapAccessor<@Nullable T>, NonNullMapAccessor<@Nullable T> {

    private static final long serialVersionUID = 1L;


    /**
     * Creates a new instance with the specified name, enum class, mask and
     * default value.
     *
     * @param name         The name of the key.
     * @param clazz        The enum class.
     * @param defaultValue The default value.
     */
    public EnumStyleableKey(@NonNull String name, @NonNull Class<T> clazz, @Nullable T defaultValue) {
        super(name, clazz, null, defaultValue);
        converter = new CssEnumConverter<>(getRawValueType(), false);
    }

    private final @NonNull Converter<T> converter;

    @Override
    public @NonNull Converter<T> getCssConverter() {
        return converter;
    }
}
