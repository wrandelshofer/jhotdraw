/* @(#)Rectangle2DStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Rectangle2D;
import org.jhotdraw.styleable.StyleableKey;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.CssRectangle2DConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;

/**
 * Rectangle2DStyleableFigureKey.
 *
 * @author werni
 */
public class Rectangle2DStyleableFigureKey extends SimpleFigureKey<Rectangle2D> implements StyleableKey<Rectangle2D> {

    private final static long serialVersionUID=1L;

    private final CssMetaData<?, Rectangle2D> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public Rectangle2DStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public Rectangle2DStyleableFigureKey(String name, Rectangle2D defaultValue) {
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
    public Rectangle2DStyleableFigureKey(String key, DirtyMask mask, Rectangle2D defaultValue) {
        super(key, Rectangle2D.class, mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
         cssMetaData = factory.createPoint2DCssMetaData(
         Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         });*/

        Function<Styleable, StyleableProperty<Rectangle2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Rectangle2D> converter
                = new StyleConverterConverterWrapper<Rectangle2D>(new CssRectangle2DConverter());
        CssMetaData<Styleable, Rectangle2D> md
                = new SimpleCssMetaData<Styleable, Rectangle2D>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?,Rectangle2D> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<Rectangle2D> converter;

    @Override
    public Converter<Rectangle2D> getConverter() {
        if (converter == null) {
            converter = new CssRectangle2DConverter();
        }
        return converter;
    }   
}
