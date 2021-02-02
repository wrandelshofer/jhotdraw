/*
 * @(#)DoubleStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.css.text.CssDoubleConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * DoubleStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class DoubleStyleableKey
        extends AbstractStyleableKey<@NonNull Double>
        implements WriteableStyleableMapAccessor<@NonNull Double>,
        NonNullMapAccessor<@NonNull Double> {
    final static long serialVersionUID = 1L;
    @NonNull
    private final CssMetaData<@NonNull Styleable, @NonNull Double> cssMetaData;

    private final Converter<@NonNull Double> converter;

    /**
     * Creates a new instance with the specified name and with 0.0 as the
     * default value.
     *
     * @param name The name of the key.
     */
    public DoubleStyleableKey(@NonNull String name) {
        this(name, 0.0);
    }


    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public DoubleStyleableKey(@NonNull String name, double defaultValue) {
        this(name, defaultValue, new CssDoubleConverter(false));
    }


    public DoubleStyleableKey(@NonNull String name, double defaultValue, @NonNull CssConverter<@NonNull Double> converter) {
        super(name, Double.class, defaultValue);

        Function<Styleable, StyleableProperty<Double>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        this.converter = converter;
        CssMetaData<@NonNull Styleable, @NonNull Double> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, Double> getCssMetaData() {
        return cssMetaData;

    }

    @NonNull
    @Override
    public Converter<@NonNull Double> getCssConverter() {
        return converter;
    }
}
