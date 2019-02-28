/* @(#)InsetsStyleableFigureKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.css.text.CssInsetsConverterOLD;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * InsetsStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class InsetsStyleableFigureKey extends AbstractStyleableFigureKey<Insets> implements WriteableStyleableMapAccessor<Insets> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, Insets> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public InsetsStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public InsetsStyleableFigureKey(String name, Insets defaultValue) {
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
    public InsetsStyleableFigureKey(String key, DirtyMask mask, Insets defaultValue) {
        super(key, Insets.class, mask, defaultValue);

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

    @Nonnull
    @Override
    public CssMetaData<?, Insets> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<Insets> converter = new CssInsetsConverterOLD(false);

    @Override
    public Converter<Insets> getConverter() {
        return converter;
    }
}
