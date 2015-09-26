/* @(#)PaintStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.paint.Paint;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.SimpleKey;
import org.jhotdraw.draw.css.StyleableKey;
import org.jhotdraw.draw.css.StyleablePropertyBean;
import org.jhotdraw.draw.DirtyBits;
import org.jhotdraw.draw.DirtyMask;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.SimpleFigureKey;
import org.jhotdraw.text.CSSPaintConverter;
import org.jhotdraw.text.CSSSizeConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;

/**
 * PaintStyleableFigureKey.
 *
 * @author werni
 */
public class PaintStyleableFigureKey extends SimpleFigureKey<Paint> implements StyleableKey<Paint> {

    private final CssMetaData cssMetaData;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key The name of the name.
     * @param metaData The CSS meta data.
     */
    public PaintStyleableFigureKey(String key) {
        this(key, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name. type parameters are given. Otherwise
     * specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public PaintStyleableFigureKey(String key, Paint defaultValue) {
        this(key, DirtyMask.of(DirtyBits.NODE), defaultValue);
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
    public PaintStyleableFigureKey(String key, DirtyMask mask, Paint defaultValue) {
        super(key, Paint.class, mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
         cssMetaData = factory.createPaintCssMetaData(
         Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         });*/

        Function<Styleable, StyleableProperty<Paint>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Paint> converter
                = new StyleConverterConverterWrapper<Paint>(new CSSPaintConverter());
        CssMetaData<Styleable, Paint> md
                = new SimpleCssMetaData<Styleable, Paint>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData createCssMetaData() {
        return cssMetaData;

    }

}
