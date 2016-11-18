/* @(#)CColorStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssCColorConverter;
import org.jhotdraw8.text.StyleConverterConverterWrapper;
import org.jhotdraw8.styleable.StyleableMapAccessor;
import org.jhotdraw8.text.CColor;

/**
 * CColorStyleableFigureKey.
 *
 * @author werni
 */
public class CColorStyleableFigureKey extends SimpleFigureKey<CColor> implements StyleableMapAccessor<CColor> {

    private final static long serialVersionUID=1L;

    private final CssMetaData<?, CColor> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public CColorStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public CColorStyleableFigureKey(String name, CColor defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name. type parameters are given. Otherwise
     * specify them in arrow brackets.
     * @param mask Dirty bit mask.
     * @param defaultValue The default value.
     */
    public CColorStyleableFigureKey(String key, DirtyMask mask, CColor defaultValue) {
        super(key, CColor.class, mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
         cssMetaData = factory.createCColorCssMetaData(
         Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         });*/

        Function<Styleable, StyleableProperty<CColor>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, CColor> converter
                = new StyleConverterConverterWrapper<CColor>(new CssCColorConverter());
        CssMetaData<Styleable, CColor> md
                = new SimpleCssMetaData<Styleable, CColor>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?,CColor> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<CColor> converter;

    @Override
    public Converter<CColor> getConverter() {
        if (converter == null) {
            converter = new CssCColorConverter();
        }
        return converter;
    }   
}
