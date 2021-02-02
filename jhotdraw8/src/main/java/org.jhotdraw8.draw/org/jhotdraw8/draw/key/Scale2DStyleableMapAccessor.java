/*
 * @(#)Scale2DStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.text.CssScale2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.function.Function;

/**
 * Scale2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class Scale2DStyleableMapAccessor extends AbstractStyleableMapAccessor<Point2D> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, Point2D> cssMetaData;
    @NonNull
    private final MapAccessor<Double> xKey;
    @NonNull
    private final MapAccessor<Double> yKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     */
    public Scale2DStyleableMapAccessor(String name, @NonNull MapAccessor<Double> xKey, @NonNull MapAccessor<Double> yKey) {
        super(name, Point2D.class, new MapAccessor<?>[]{xKey, yKey}, new Point2D(xKey.getDefaultValue(), yKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<Point2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Point2D> cnvrtr
                = new StyleConverterAdapter<>(getCssConverter());
        CssMetaData<Styleable, Point2D> md
                = new SimpleCssMetaData<>(property, function,
                cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.xKey = xKey;
        this.yKey = yKey;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, Point2D> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<Point2D> converter;

    @Override
    public @NonNull Converter<Point2D> getCssConverter() {
        if (converter == null) {
            converter = new CssScale2DConverter();
        }
        return converter;
    }

    @NonNull
    @Override
    public Point2D get(@NonNull Map<? super Key<?>, Object> a) {
        return new Point2D(xKey.get(a), yKey.get(a));
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @NonNull Point2D value) {
        xKey.put(a, value.getX());
        yKey.put(a, value.getY());
    }

    @NonNull
    @Override
    public Point2D remove(@NonNull Map<? super Key<?>, Object> a) {
        Point2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        return oldValue;
    }
}
