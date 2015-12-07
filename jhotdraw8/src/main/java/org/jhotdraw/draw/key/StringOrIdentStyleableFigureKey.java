/* @(#)StringStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.StyleConverterConverterWrapper;
import org.jhotdraw.styleable.StyleableMapAccessor;
import org.jhotdraw.text.CssStringOrIdentConverter;

/**
 * StringStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class StringOrIdentStyleableFigureKey extends SimpleFigureKey<String> implements StyleableMapAccessor<String> {

    final static long serialVersionUID = 1L;
    private final CssMetaData<? extends Styleable, String> cssMetaData;

    /**
     * Creates a new instance with the specified name and with an empty String as the
     * default value.
     *
     * @param name The name of the key.
     */
    public StringOrIdentStyleableFigureKey(String name) {
        this(name, "");
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public StringOrIdentStyleableFigureKey(String name, String defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public StringOrIdentStyleableFigureKey(String name, DirtyMask mask, String defaultValue) {
        super(name, String.class, false, mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
         cssMetaData = factory.createSizeCssMetaData(
         Figure.JHOTDRAW_CSS_PREFIX + getCssName(), s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         });*/

        Function<Styleable, StyleableProperty<String>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, String> converter
                = new StyleConverterConverterWrapper<String>(getConverter());
        CssMetaData<Styleable, String> md
                = new SimpleCssMetaData<Styleable, String>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

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
