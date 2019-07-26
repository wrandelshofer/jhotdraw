/*
 * @(#)NullableCssColorStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * NullableCssColorStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NullableCssColorStyleableKey extends AbstractStyleableKey<CssColor>
        implements WriteableStyleableMapAccessor<CssColor> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, CssColor> cssMetaData;
    private final Converter<CssColor> converter = new CssColorConverter(true);

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public NullableCssColorStyleableKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public NullableCssColorStyleableKey(String name, CssColor defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param mask         Dirty bit mask.
     * @param defaultValue The default value.
     */
    public NullableCssColorStyleableKey(String key, DirtyMask mask, CssColor defaultValue) {
        super(key, CssColor.class, defaultValue);

        Function<Styleable, StyleableProperty<CssColor>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        CssMetaData<Styleable, CssColor> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(this.converter), defaultValue, inherits);
        cssMetaData = md;
    }

    @Nonnull
    @Override
    public CssMetaData<?, CssColor> getCssMetaData() {
        return cssMetaData;

    }

    @Override
    public Converter<CssColor> getConverter() {
        return converter;
    }
}
