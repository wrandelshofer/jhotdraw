/*
 * @(#)ListStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ListKey;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.draw.key.SimpleCssMetaData;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

public class ListStyleableKey<T> extends ListKey<T>
        implements WriteableStyleableMapAccessor<@NonNull ImmutableList<T>>, NonNullMapAccessor<@NonNull ImmutableList<T>> {
    private final static long serialVersionUID = 0L;
    @NonNull
    private final Converter<@NonNull ImmutableList<T>> converter;
    @NonNull
    private final CssMetaData<? extends Styleable, @NonNull ImmutableList<T>> cssMetaData;
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

        Function<Styleable, StyleableProperty<ImmutableList<T>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        final StyleConverter<String, ImmutableList<T>> styleConverter
                = new StyleConverterAdapter<>(this.converter);
        cssMetaData = new SimpleCssMetaData<>(key, function, styleConverter, defaultValue, false);
        this.cssName = cssName;

    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, ImmutableList<T>> getCssMetaData() {
        return cssMetaData;
    }

    @NonNull
    @Override
    public Converter<ImmutableList<T>> getCssConverter() {
        return converter;
    }

    @NonNull
    @Override
    public String getCssName() {
        return cssName;
    }
}
