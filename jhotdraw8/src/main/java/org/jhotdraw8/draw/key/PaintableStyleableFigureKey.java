/* @(#)PaintStyleableFigureKey.java
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
import org.jhotdraw8.text.CssPaintableConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.styleable.StyleableMapAccessor;
import org.jhotdraw8.text.Paintable;

/**
 * PaintStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class PaintableStyleableFigureKey extends SimpleFigureKey<Paintable> implements StyleableMapAccessor<Paintable> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, Paintable> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public PaintableStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public PaintableStyleableFigureKey(String name, Paintable defaultValue) {
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
    public PaintableStyleableFigureKey(String key, DirtyMask mask, Paintable defaultValue) {
        super(key, Paintable.class, mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
         cssMetaData = factory.createPaintCssMetaData(
         Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         });*/

        Function<Styleable, StyleableProperty<Paintable>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Paintable> converter
                = new StyleConverterAdapter<Paintable>(new CssPaintableConverter());
        CssMetaData<Styleable, Paintable> md
                = new SimpleCssMetaData<Styleable, Paintable>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?, Paintable> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<Paintable> converter;

    @Override
    public Converter<Paintable> getConverter() {
        if (converter == null) {
            converter = new CssPaintableConverter();
        }
        return converter;
    }
}
