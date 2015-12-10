/* @(#)FontStyleableFigureKey.java
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
import org.jhotdraw.text.CssFFontConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;
import org.jhotdraw.styleable.StyleableMapAccessor;
import org.jhotdraw.text.FFont;

/**
 * FontStyleableFigureKey.
 *
 * @author werni
 */
public class FontStyleableFigureKey extends SimpleFigureKey<FFont> implements StyleableMapAccessor<FFont> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, FFont> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public FontStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public FontStyleableFigureKey(String name, FFont defaultValue) {
        super(name, FFont.class, //
                DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT),//
                defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
         cssMetaData = factory.createFontCssMetaData(
         Figure.JHOTDRAW_CSS_PREFIX + getCssName(), s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         });
         */
        Function<Styleable, StyleableProperty<FFont>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, FFont> converter
                = new StyleConverterConverterWrapper<FFont>(new CssFFontConverter());
        CssMetaData<Styleable, FFont> md
                = new SimpleCssMetaData<Styleable, FFont>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?, FFont> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<FFont> converter;

    @Override
    public Converter<FFont> getConverter() {
        if (converter == null) {
            converter = new CssFFontConverter();
        }
        return converter;
    }
}
