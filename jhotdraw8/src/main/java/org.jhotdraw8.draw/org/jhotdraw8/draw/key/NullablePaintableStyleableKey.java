/*
 * @(#)NullablePaintableStyleableKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.text.CssPaintableConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * PaintStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class NullablePaintableStyleableKey extends AbstractStyleableKey<Paintable> implements WriteableStyleableMapAccessor<Paintable> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, Paintable> cssMetaData;
    private Converter<Paintable> converter;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public NullablePaintableStyleableKey(@NonNull String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *  @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public NullablePaintableStyleableKey(@NonNull String key, Paintable defaultValue) {
        super(key, Paintable.class, defaultValue);

        Function<Styleable, StyleableProperty<Paintable>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        converter = new CssPaintableConverter(true);
        CssMetaData<Styleable, Paintable> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), defaultValue, inherits);
        cssMetaData = md;
    }

    @NonNull
    @Override
    public CssMetaData<?, Paintable> getCssMetaData() {
        return cssMetaData;

    }

    @Override
    public Converter<Paintable> getConverter() {
        return converter;
    }
}
