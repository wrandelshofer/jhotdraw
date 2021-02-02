/*
 * @(#)InsetsStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.InsetsConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * InsetsStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class InsetsStyleableKey extends AbstractStyleableKey<Insets> implements WriteableStyleableMapAccessor<Insets> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, Insets> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public InsetsStyleableKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *  @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public InsetsStyleableKey(String key, Insets defaultValue) {
        super(key, Insets.class, defaultValue);

        Function<Styleable, StyleableProperty<Insets>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        CssMetaData<Styleable, Insets> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(this.converter), defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, Insets> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<Insets> converter = new InsetsConverter(false);

    @NonNull
    @Override
    public Converter<Insets> getCssConverter() {
        return converter;
    }
}
