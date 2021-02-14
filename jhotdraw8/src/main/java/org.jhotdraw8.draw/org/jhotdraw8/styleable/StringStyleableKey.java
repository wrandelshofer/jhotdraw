/*
 * @(#)StringStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.SimpleNullableKey;
import org.jhotdraw8.css.text.CssStringConverter;
import org.jhotdraw8.draw.key.SimpleCssMetaData;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

public class StringStyleableKey extends SimpleNullableKey<String> implements WriteableStyleableMapAccessor<String> {
    private static final long serialVersionUID = 0L;
    private final @NonNull String cssName;
    private final @NonNull CssMetaData<? extends Styleable, String> cssMetaData;
    private final CssStringConverter converter = new CssStringConverter();

    public StringStyleableKey(String key) {
        this(key, null);
    }

    public StringStyleableKey(String key, String defaultValue) {
        super(key, String.class, defaultValue);

        Function<Styleable, StyleableProperty<String>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        final StyleConverter<String, String> styleConverter
                = new StyleConverterAdapter<>(this.converter);
        cssMetaData = new SimpleCssMetaData<>(key, function, styleConverter, defaultValue, false);
        cssName = ReadOnlyStyleableMapAccessor.toCssName(getName());
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, String> getCssMetaData() {
        return cssMetaData;
    }

    @Override
    public @NonNull Converter<String> getCssConverter() {
        return converter;
    }

    @Override
    public @NonNull String getCssName() {
        return cssName;
    }
}
