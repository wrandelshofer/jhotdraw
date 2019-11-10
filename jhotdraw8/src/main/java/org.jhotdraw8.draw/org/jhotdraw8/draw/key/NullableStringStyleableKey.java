/*
 * @(#)NullableStringStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.CssStringConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * NullableStringStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class NullableStringStyleableKey extends AbstractStyleableKey<String>
        implements WriteableStyleableMapAccessor<String> {

    final static long serialVersionUID = 1L;
    @NonNull
    private final CssMetaData<? extends Styleable, String> cssMetaData;

    /**
     * Creates a new instance with the specified name and with a null String
     * as the default value.
     *
     * @param name The name of the key.
     */
    public NullableStringStyleableKey(@NonNull String name) {
        this(null, name, null);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     */
    public NullableStringStyleableKey(String namespace, @NonNull String name) {
        this(namespace, name, null);
    }

    public NullableStringStyleableKey(String namespace, @NonNull String name, String helpText) {
        super(namespace, name, String.class, true, null);
        converter = new CssStringConverter(true, '\'', helpText);
        Function<Styleable, StyleableProperty<String>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, String> converter = new StyleConverterAdapter<>(this.converter);
        CssMetaData<Styleable, String> md
                = new SimpleCssMetaData<>(property, function,
                converter, null, inherits);
        cssMetaData = md;
    }

    @NonNull
    @Override
    public CssMetaData<? extends Styleable, String> getCssMetaData() {
        return cssMetaData;

    }

    @NonNull
    private final CssStringConverter converter;

    @NonNull
    @Override
    public Converter<String> getConverter() {
        return converter;
    }
}
