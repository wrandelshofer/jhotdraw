/*
 * @(#)EnumStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

import java.util.Objects;

/**
 * EnumStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class EnumStyleableKey<@NonNull T extends Enum<T>> extends AbstractStyleableKey<@NonNull T>
        implements WritableStyleableMapAccessor<@NonNull T>, NonNullMapAccessor<@NonNull T> {

    private static final long serialVersionUID = 1L;


    /**
     * Creates a new instance with the specified name, enum class, mask and
     * default value.
     *  @param name         The name of the key.
     * @param clazz        The enum class.
     * @param defaultValue The default value.
     */
    public EnumStyleableKey(@NonNull String name, @NonNull Class<T> clazz, @NonNull T defaultValue) {
        super(name, clazz, defaultValue);

        Objects.requireNonNull(defaultValue, "defaultValue is null");

        converter = new CssEnumConverter<>(getRawValueType(), false);
    }

    private final @NonNull Converter<T> converter;

    @Override
    public @NonNull Converter<T> getCssConverter() {
        return converter;
    }
}
