/* @(#)Point2DStyleableMapAccessor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import java.util.Map;
import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Point2D;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.styleable.AbstractStyleableFigureMapAccessor;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.CssPoint2DConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;

/**
 * Point2DStyleableMapAccessor.
 *
 * @author werni
 */
public class Point2DStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<Point2D> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, Point2D> cssMetaData;
    private final MapAccessor<Double> xKey;
    private final MapAccessor<Double> yKey;

    /**
     * Creates a new instance with the specified name.
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     */
    public Point2DStyleableMapAccessor(String name, MapAccessor<Double> xKey, MapAccessor<Double> yKey) {
        super(name, Point2D.class, new MapAccessor<?>[]{xKey, yKey}, new Point2D(xKey.getDefaultValue(), yKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<Point2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Point2D> cnvrtr
                = new StyleConverterConverterWrapper<>(getConverter());
        CssMetaData<Styleable, Point2D> md
                = new SimpleCssMetaData<>(property, function,
                        cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.xKey = xKey;
        this.yKey = yKey;
    }

    @Override
    public CssMetaData<?, Point2D> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<Point2D> converter;

    @Override
    public Converter<Point2D> getConverter() {
        if (converter == null) {
            converter = new CssPoint2DConverter();
        }
        return converter;
    }

    @Override
    public Point2D get(Map<? super Key<?>, Object> a) {
        return new Point2D(xKey.get(a), yKey.get(a));
    }

    @Override
    public Point2D put(Map<? super Key<?>, Object> a, Point2D value) {
        Point2D oldValue = get(a);
        xKey.put(a, value.getX());
        yKey.put(a, value.getY());
        return oldValue;
    }

    @Override
    public Point2D remove(Map<? super Key<?>, Object> a) {
        Point2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        return oldValue;
    }
}
