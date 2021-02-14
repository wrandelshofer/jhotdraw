/*
 * @(#)ObjectStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.AbstractKey;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.text.Converter;

import java.lang.reflect.Type;

public class ObjectStyleableKey<T> extends AbstractKey<T> implements WriteableStyleableMapAccessor<T> {
    private static final long serialVersionUID = 0L;

    private final @NonNull Converter<T> converter;

    public ObjectStyleableKey(String name, Class<T> clazz, @NonNull Converter<T> converter) {
        this(name, clazz, null, converter);
    }

    public ObjectStyleableKey(String name, TypeToken<T> clazz, @NonNull Converter<T> converter) {
        this(name, clazz, null, converter);
    }

    public ObjectStyleableKey(String name, TypeToken<T> clazz, T defaultValue, @NonNull Converter<T> converter) {
        this(name, clazz.getType(), defaultValue, converter);
    }

    public ObjectStyleableKey(String name, Type clazz, T defaultValue, @NonNull Converter<T> converter) {
        super(name, clazz, defaultValue == null, false, defaultValue);
        this.converter = converter;
    }

    @Override
    public @NonNull Converter<T> getCssConverter() {
        return converter;
    }

    @Override
    public @NonNull String getCssName() {
        return getName();
    }
}
