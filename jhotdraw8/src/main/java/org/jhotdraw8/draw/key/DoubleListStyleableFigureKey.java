/* @(#)DoubleListStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssDoubleListConverter;
import org.jhotdraw8.text.StyleConverterConverterWrapper;
import org.jhotdraw8.styleable.StyleableMapAccessor;

/**
 * DoubleListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class DoubleListStyleableFigureKey extends SimpleFigureKey<ImmutableObservableList<Double>> implements StyleableMapAccessor<ImmutableObservableList<Double>> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, ImmutableObservableList<Double>> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public DoubleListStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public DoubleListStyleableFigureKey(String name, ImmutableObservableList<Double> defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public DoubleListStyleableFigureKey(String name, DirtyMask mask, ImmutableObservableList<Double> defaultValue) {
        super(name, ImmutableObservableList.class, new Class<?>[]{Double.class}, mask, defaultValue);

        Function<Styleable, StyleableProperty<ImmutableObservableList<Double>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, ImmutableObservableList<Double>> converter
                = new StyleConverterConverterWrapper<ImmutableObservableList<Double>>(new CssDoubleListConverter());
        CssMetaData<Styleable, ImmutableObservableList<Double>> md
                = new SimpleCssMetaData<Styleable, ImmutableObservableList<Double>>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?, ImmutableObservableList<Double>> getCssMetaData() {
        return cssMetaData;
    }

    private Converter<ImmutableObservableList<Double>> converter;

    @Override
    public Converter<ImmutableObservableList<Double>> getConverter() {
        if (converter == null) {
            converter = new CssDoubleListConverter();
        }
        return converter;
    }

}
