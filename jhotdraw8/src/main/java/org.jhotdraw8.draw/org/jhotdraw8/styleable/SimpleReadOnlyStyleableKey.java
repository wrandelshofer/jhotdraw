/*
 * @(#)SimpleReadOnlyStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.AbstractKey;
import org.jhotdraw8.text.Converter;

import java.lang.reflect.Type;

/**
 * SimpleReadOnlyStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class SimpleReadOnlyStyleableKey<T> extends AbstractKey<T> implements ReadOnlyStyleableMapAccessor<T> {
    private final @NonNull String cssName;
    private static final long serialVersionUID = 1L;

    protected final @NonNull Converter<T> converter;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key       The name of the name.
     * @param clazz     The type of the value.
     * @param converter the converter
     */
    public SimpleReadOnlyStyleableKey(@NonNull String key, @NonNull Type clazz, @NonNull Converter<T> converter) {
        this(key, clazz, converter, null);
    }


    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *  @param key          The name of the name.
     * @param clazz        The type of the value.
     * @param converter    the converter
     * @param defaultValue The default value.
     */
    public SimpleReadOnlyStyleableKey(@NonNull String key, @NonNull Type clazz,
                                      @NonNull Converter<T> converter,
                                      @Nullable T defaultValue) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), clazz, converter, defaultValue);

    }

    public SimpleReadOnlyStyleableKey(@NonNull String key, @NonNull String cssName, @NonNull Type clazz,
                                      @NonNull Converter<T> converter,
                                      @Nullable T defaultValue) {
        super(key, clazz, defaultValue == null, defaultValue);
        this.converter = converter;
        this.cssName = cssName;
    }

    @Override
    public @NonNull Converter<T> getCssConverter() {
        return converter;
    }



    public @NonNull String getCssName() {
        return cssName;
    }

}
