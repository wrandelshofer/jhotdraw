/*
 * @(#)StringOrIdentStyleableKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssStringOrIdentConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * This key has a string value which can be given as a CSS "IDENT"-token or
 * as a CSS "STRING"-token.
 *
 * @author Werner Randelshofer
 */
public class StringOrIdentStyleableKey extends AbstractStyleableKey<String>
        implements WriteableStyleableMapAccessor<String>, NonNullMapAccessor<String> {

    final static long serialVersionUID = 1L;
    @NonNull
    private final CssMetaData<? extends Styleable, String> cssMetaData;

    /**
     * Creates a new instance with the specified name and with an empty String
     * as the default value.
     *
     * @param name The name of the key.
     */
    public StringOrIdentStyleableKey(@NonNull String name) {
        this(name, "");
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *  @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public StringOrIdentStyleableKey(@NonNull String name, String defaultValue) {
        super(null, name, String.class, false, defaultValue);

        Function<Styleable, StyleableProperty<String>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, String> converter
                = new StyleConverterAdapter<>(getConverter());
        CssMetaData<Styleable, String> md
                = new SimpleCssMetaData<>(property, function,
                converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @NonNull
    @Override
    public CssMetaData<? extends Styleable, String> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<String> converter;

    @Override
    public Converter<String> getConverter() {
        if (converter == null) {
            converter = new CssStringOrIdentConverter();
        }
        return converter;
    }
}
