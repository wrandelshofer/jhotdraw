/*
 * @(#)ObjectStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.draw.key.SimpleCssMetaData;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

public class ObjectStyleableKey<T> extends ObjectKey<T> implements WriteableStyleableMapAccessor<T> {
    private final static long serialVersionUID = 0L;

    @NonNull
    private final Converter<T> converter;
    @NonNull
    private final CssMetaData<? extends Styleable, T> cssMetaData;

    public ObjectStyleableKey(String name, Class<T> clazz, @NonNull Converter<T> converter) {
        this(name, clazz, null, converter);
    }

    public ObjectStyleableKey(String name, Class<T> clazz, T defaultValue, @NonNull Converter<T> converter) {
        this(name, clazz, null, defaultValue, converter);
    }

    public ObjectStyleableKey(String name, Class<?> clazz, @Nullable Class<?>[] typeParameters, @Nullable T defaultValue, @NonNull Converter<T> converter) {
        this(name, clazz, typeParameters, true, defaultValue, converter);
    }

    public ObjectStyleableKey(String name, Class<?> clazz, Class<?>[] typeParameters, boolean isNullable, T defaultValue, @NonNull Converter<T> converter) {
        this(name, clazz, typeParameters, isNullable, false, defaultValue, converter);
    }

    public ObjectStyleableKey(@Nullable String name, @Nullable Class<?> clazz, @Nullable Class<?>[] typeParameters, boolean isNullable, boolean isTransient, @Nullable T defaultValue, @NonNull Converter<T> converter) {
        super(name, clazz, typeParameters, isNullable, isTransient, defaultValue);
        this.converter = converter;

        Function<Styleable, StyleableProperty<T>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        final StyleConverter<String, T> styleConverter
                = new StyleConverterAdapter<>(this.converter);
        cssMetaData = new SimpleCssMetaData<>(name, function, styleConverter, defaultValue, false);
    }

    @Nullable
    @Override
    public CssMetaData<? extends Styleable, T> getCssMetaData() {
        return cssMetaData;
    }

    @NonNull
    @Override
    public Converter<T> getConverter() {
        return converter;
    }

    @NonNull
    @Override
    public String getCssName() {
        return getName();
    }
}
