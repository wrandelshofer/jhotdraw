/* @(#)SVGPathStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.shape.SVGPath;
import org.jhotdraw.styleable.StyleableKey;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.CssSVGPathConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;

/**
 * SVGPathStyleableFigureKey.
 *
 * @author werni
 */
public class SVGPathStyleableFigureKey extends SimpleFigureKey<SVGPath> implements StyleableKey<SVGPath> {

    private final static long serialVersionUID=1L;

    private final CssMetaData<?, SVGPath> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public SVGPathStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public SVGPathStyleableFigureKey(String name, SVGPath defaultValue) {
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
    public SVGPathStyleableFigureKey(String key, DirtyMask mask, SVGPath defaultValue) {
        super(key, SVGPath.class, mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
         cssMetaData = factory.createSVGPathCssMetaData(
         Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         });*/

        Function<Styleable, StyleableProperty<SVGPath>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, SVGPath> converter
                = new StyleConverterConverterWrapper<SVGPath>(new CssSVGPathConverter());
        CssMetaData<Styleable, SVGPath> md
                = new SimpleCssMetaData<Styleable, SVGPath>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?,SVGPath> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<SVGPath> converter;

    @Override
    public Converter<SVGPath> getConverter() {
        if (converter == null) {
            converter = new CssSVGPathConverter();
        }
        return converter;
    }   
}
