/*
 * @(#)ListStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.collection.SimpleNonNullListKey;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.text.Converter;

public class ListStyleableKey<T> extends SimpleNonNullListKey<T>
        implements WriteableStyleableMapAccessor<@NonNull ImmutableList<T>>, NonNullMapAccessor<@NonNull ImmutableList<T>> {
    private static final long serialVersionUID = 0L;
    private final @NonNull Converter<@NonNull ImmutableList<T>> converter;
    private final String cssName;

    public ListStyleableKey(@NonNull String key, @NonNull TypeToken<ImmutableList<T>> type, @NonNull CssConverter<T> converter) {
        this(key, type, ImmutableLists.emptyList(), converter);
    }

    public ListStyleableKey(@NonNull String key, @NonNull TypeToken<ImmutableList<T>> type, @NonNull CssListConverter<T> converter) {
        this(key, type, ImmutableLists.emptyList(), converter);
    }

    public ListStyleableKey(@NonNull String key, @NonNull TypeToken<ImmutableList<T>> type, @NonNull ImmutableList<T> defaultValue, @NonNull CssConverter<T> converter) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), type, defaultValue, converter);
    }

    public ListStyleableKey(@NonNull String key, @NonNull TypeToken<ImmutableList<T>> type, @NonNull ImmutableList<T> defaultValue, @NonNull CssListConverter<T> converter) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), type, defaultValue, converter);
    }

    public ListStyleableKey(@NonNull String key, String cssName, @NonNull TypeToken<ImmutableList<T>> type, @NonNull ImmutableList<T> defaultValue, @NonNull CssConverter<T> converter) {
        this(key, cssName, type, defaultValue, new CssListConverter<>(converter));
    }

    public ListStyleableKey(@NonNull String key, String cssName, @NonNull TypeToken<ImmutableList<T>> type, @NonNull ImmutableList<T> defaultValue, @NonNull CssListConverter<T> converter) {
        super(key, type, defaultValue);
        this.converter = converter;
        this.cssName = cssName;
    }

    @Override
    public @NonNull Converter<ImmutableList<T>> getCssConverter() {
        return converter;
    }

    @Override
    public @NonNull String getCssName() {
        return cssName;
    }
}
