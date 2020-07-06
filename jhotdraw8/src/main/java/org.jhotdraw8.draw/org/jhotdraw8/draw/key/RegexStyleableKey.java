/*
 * @(#)RegexStyleableKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.CssRegexConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.RegexReplace;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * RegexStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class RegexStyleableKey extends AbstractStyleableKey<RegexReplace> implements WriteableStyleableMapAccessor<RegexReplace> {

    final static long serialVersionUID = 1L;
    @NonNull
    private final CssRegexConverter converter;
    @NonNull
    private final CssMetaData<? extends Styleable, RegexReplace> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public RegexStyleableKey(String name) {
        this(name, new RegexReplace());
    }


    /**
     * Creates a new instance with the specified name and default value. The
     * value is nullable.
     *  @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public RegexStyleableKey(String name, RegexReplace defaultValue) {
        this(name, true, defaultValue);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *  @param name         The name of the key.
     * @param nullable     whether the value is nullable
     * @param defaultValue The default value.
     */
    public RegexStyleableKey(String name, boolean nullable, RegexReplace defaultValue) {
        super(null, name, RegexReplace.class, nullable, defaultValue);

        Function<Styleable, StyleableProperty<RegexReplace>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, RegexReplace> cnvrtr
                = new StyleConverterAdapter<>(getConverter());
        CssMetaData<Styleable, RegexReplace> md
                = new SimpleCssMetaData<>(property, function,
                cnvrtr, defaultValue, inherits);
        cssMetaData = md;
        converter = new CssRegexConverter(isNullable());
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, RegexReplace> getCssMetaData() {
        return cssMetaData;

    }

    @NonNull
    @Override
    public Converter<RegexReplace> getConverter() {
        return converter;
    }
}
