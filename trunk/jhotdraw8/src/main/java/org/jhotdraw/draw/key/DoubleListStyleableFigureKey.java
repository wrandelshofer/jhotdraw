/* @(#)DoubleListStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import javafx.beans.property.Property;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.geometry.Insets;
import javafx.util.Pair;
import org.jhotdraw.draw.DirtyBits;
import org.jhotdraw.draw.DirtyMask;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.SimpleFigureKey;
import org.jhotdraw.draw.css.DoubleListStyleConverter;
import org.jhotdraw.draw.css.StyleableKey;
import org.jhotdraw.draw.css.StyleablePropertyBean;

/**
 * DoubleListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class DoubleListStyleableFigureKey extends SimpleFigureKey<List<Double>> implements StyleableKey<List<Double>> {

    private final CssMetaData cssMetaData;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key The name of the name.
     * @param metaData The CSS meta data.
     */
    public DoubleListStyleableFigureKey(String key) {
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
    public DoubleListStyleableFigureKey(String key, List<Double> defaultValue) {
        this(key, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name. type parameters are given. Otherwise
     * specify them in arrow brackets.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public DoubleListStyleableFigureKey(String key, DirtyMask mask, List<Double> defaultValue) {
        super(key, List.class, "<Double>", mask, defaultValue);

        StyleablePropertyFactory factory = new StyleablePropertyFactory(null);

        Function<Styleable, StyleableProperty<List<Double>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<ParsedValue[], List<Double>> converter
                = DoubleListStyleConverter.getInstance();
        CssMetaData<Styleable, List<Double>> md
                = new SimpleCssMetaData<Styleable, List<Double>>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData createCssMetaData() {
        return cssMetaData;

    }
}
