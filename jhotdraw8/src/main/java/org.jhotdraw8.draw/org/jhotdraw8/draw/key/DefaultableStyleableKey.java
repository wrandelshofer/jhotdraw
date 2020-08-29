/*
 * @(#)ListStyleableKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssDefaultableValue;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.css.text.CssDefaultableValueConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * TListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class DefaultableStyleableKey<T> extends AbstractStyleableKey<@NonNull CssDefaultableValue<T>>
        implements WriteableStyleableMapAccessor<@NonNull CssDefaultableValue<T>>,
        NonNullMapAccessor<@NonNull CssDefaultableValue<T>> {

    private static final long serialVersionUID = 1L;

    private final @NonNull CssMetaData<?, @NonNull CssDefaultableValue<T>> cssMetaData;
    private final @NonNull Converter<@NonNull CssDefaultableValue<T>> converter;
    private final @Nullable T initialValue;


    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param clazz        the class of the type
     * @param converter    String converter for a list element
     * @param defaultValue The default value.
     */
    public DefaultableStyleableKey(@NonNull String name, @NonNull Class<T> clazz, @NonNull CssConverter<T> converter, @NonNull CssDefaultableValue<T> defaultValue, @Nullable T initialValue) {
        super(name, CssDefaultableValue.class, new Class<?>[]{clazz}, defaultValue);

        Function<Styleable, StyleableProperty<CssDefaultableValue<T>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        this.converter = new CssDefaultableValueConverter<>(converter);
        CssMetaData<Styleable, CssDefaultableValue<T>> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(this.converter), defaultValue, inherits);
        cssMetaData = md;
        this.initialValue = initialValue;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, CssDefaultableValue<T>> getCssMetaData() {
        return cssMetaData;
    }

    @Override
    public @NonNull Converter<CssDefaultableValue<T>> getCssConverter() {
        return converter;
    }

    public @Nullable T getInitialValue() {
        return initialValue;
    }

}
