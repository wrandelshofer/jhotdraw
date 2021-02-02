/*
 * @(#)BooleanStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssBooleanConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * BooleanStyleableKey (not nullable).
 *
 * @author Werner Randelshofer
 */
public class BooleanStyleableKey extends AbstractStyleableKey<@NonNull Boolean>
        implements WriteableStyleableMapAccessor<@NonNull Boolean>,
        NonNullMapAccessor<@NonNull Boolean> {

    final static long serialVersionUID = 1L;
    private final CssMetaData<? extends Styleable, @NonNull Boolean> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public BooleanStyleableKey(@NonNull String name) {
        this(name, false);
    }


    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public BooleanStyleableKey(@NonNull String key, boolean defaultValue) {
        this(null, key, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public BooleanStyleableKey(@Nullable String namespace, @NonNull String key, boolean defaultValue) {
        super(namespace, key, Boolean.class, false, defaultValue);

        StyleablePropertyFactory<? extends Styleable> factory = new StyleablePropertyFactory<>(null);
        cssMetaData = factory.createBooleanCssMetaData(
                Figure.JHOTDRAW_CSS_PREFIX + getCssName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, @NonNull Boolean> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<@NonNull Boolean> converter;

    @Override
    public @NonNull Converter<@NonNull Boolean> getCssConverter() {
        if (converter == null) {
            converter = new CssBooleanConverter(isNullable());
        }
        return converter;
    }
}
