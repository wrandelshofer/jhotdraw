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
import javafx.geometry.Point2D;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.text.CssPoint2DListConverter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * DoubleListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class Point2DListStyleableFigureKey extends SimpleFigureKey<ImmutableObservableList<Point2D>> implements WriteableStyleableMapAccessor<ImmutableObservableList<Point2D>> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, ImmutableObservableList<Point2D>> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public Point2DListStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public Point2DListStyleableFigureKey(String name, ImmutableObservableList<Point2D> defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public Point2DListStyleableFigureKey(String name, DirtyMask mask, ImmutableObservableList<Point2D> defaultValue) {
        super(name, ImmutableObservableList.class, new Class<?>[]{Point2D.class}, mask, defaultValue);

        Function<Styleable, StyleableProperty<ImmutableObservableList<Point2D>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, ImmutableObservableList<Point2D>> converter
                = new StyleConverterAdapter<ImmutableObservableList<Point2D>>(new CssPoint2DListConverter());
        CssMetaData<Styleable, ImmutableObservableList<Point2D>> md
                = new SimpleCssMetaData<Styleable, ImmutableObservableList<Point2D>>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?, ImmutableObservableList<Point2D>> getCssMetaData() {
        return cssMetaData;
    }

    private Converter<ImmutableObservableList<Point2D>> converter;

    @Override
    public Converter<ImmutableObservableList<Point2D>> getConverter() {
        if (converter == null) {
            converter = new CssPoint2DListConverter();
        }
        return converter;
    }

}
