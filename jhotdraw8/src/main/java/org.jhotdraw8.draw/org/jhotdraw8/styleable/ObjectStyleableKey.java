/*
 * @(#)ObjectStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.draw.key.SimpleCssMetaData;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.lang.reflect.Type;
import java.util.function.Function;

public class ObjectStyleableKey<T> extends ObjectKey<T> implements WriteableStyleableMapAccessor<T> {
    private static final long serialVersionUID = 0L;

    private final @NonNull Converter<T> converter;
    private final @NonNull CssMetaData<? extends Styleable, T> cssMetaData;

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

        Function<Styleable, StyleableProperty<T>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        final StyleConverter<String, T> styleConverter
                = new StyleConverterAdapter<>(this.converter);
        cssMetaData = new SimpleCssMetaData<>(name, function, styleConverter, defaultValue, false);
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, T> getCssMetaData() {
        return cssMetaData;
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
