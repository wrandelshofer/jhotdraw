/*
 * @(#)CssPoint2DStyleableKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.css.text.CssPoint2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * Non-null CssPoint2DStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class CssPoint2DStyleableKey extends AbstractStyleableKey<CssPoint2D>
        implements WriteableStyleableMapAccessor<CssPoint2D>, NonNullMapAccessor<CssPoint2D> {

    private final static long serialVersionUID = 1L;
    private final Converter<CssPoint2D> converter;

    @NonNull
    private final CssMetaData<?, CssPoint2D> cssMetaData;

    /**
     * Creates a new instance with the specified name and with 0,0 as the
     * default value.
     *
     * @param name The name of the key.
     */
    public CssPoint2DStyleableKey(@NonNull String name) {
        this(name, CssPoint2D.ZERO);
    }



    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *  @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public CssPoint2DStyleableKey(@NonNull String key, @NonNull CssPoint2D defaultValue) {
        this(key, defaultValue, new CssPoint2DConverter(false));
    }

    public CssPoint2DStyleableKey(@NonNull String key, CssPoint2D defaultValue, CssConverter<CssPoint2D> converter) {
        super(key, CssPoint2D.class, defaultValue);

        Function<Styleable, StyleableProperty<CssPoint2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        this.converter = converter;
        final StyleConverter<String, CssPoint2D> c
                = new StyleConverterAdapter<>(new CssPoint2DConverter(false));
        CssMetaData<Styleable, CssPoint2D> md
                = new SimpleCssMetaData<>(property, function,
                c, defaultValue, inherits);
        cssMetaData = md;
    }


    @Override
    public Converter<CssPoint2D> getConverter() {
        return converter;
    }

    @NonNull
    @Override
    public CssMetaData<?, CssPoint2D> getCssMetaData() {
        return cssMetaData;

    }
}
