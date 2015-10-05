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
import javafx.scene.text.Font;
import org.jhotdraw.draw.css.StyleableKey;
import org.jhotdraw.draw.css.StyleablePropertyBean;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.text.CSSFontConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;

/**
 * FontStyleableFigureKey.
 *
 * @author werni
 */
public class FontStyleableFigureKey extends SimpleFigureKey<Font> implements StyleableKey<Font> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, Font> cssMetaData;

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
    public FontStyleableFigureKey(String name, Font defaultValue) {
        super(name, Font.class, //
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
        Function<Styleable, StyleableProperty<Font>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Font> converter
                = new StyleConverterConverterWrapper<Font>(new CSSFontConverter());
        CssMetaData<Styleable, Font> md
                = new SimpleCssMetaData<Styleable, Font>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?, Font> getCssMetaData() {
        return cssMetaData;

    }

}
