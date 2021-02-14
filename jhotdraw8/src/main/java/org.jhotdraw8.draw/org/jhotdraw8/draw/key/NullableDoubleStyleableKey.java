/*
 * @(#)NullableDoubleStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.css.text.CssDoubleConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * NullableDoubleStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class NullableDoubleStyleableKey extends AbstractStyleableKey<Double> implements WriteableStyleableMapAccessor<Double> {
    static final long serialVersionUID = 1L;
    private final @NonNull CssMetaData<? extends Styleable, Double> cssMetaData;

    private final Converter<Double> converter;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public NullableDoubleStyleableKey(@NonNull String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *  @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public NullableDoubleStyleableKey(@NonNull String name, Double defaultValue) {
        this(name, defaultValue, new CssDoubleConverter(true));
    }


    public NullableDoubleStyleableKey(@NonNull String name, Double defaultValue, CssConverter<Double> converter) {
        super(name, Double.class, defaultValue);

        Function<Styleable, StyleableProperty<Double>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        this.converter = converter;
        CssMetaData<Styleable, Double> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, Double> getCssMetaData() {
        return cssMetaData;

    }

    @Override
    public @NonNull Converter<Double> getCssConverter() {
        return converter;
    }
}
