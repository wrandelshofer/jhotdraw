/*
 * @(#)Point2DStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.Point2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Point2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class Point2DStyleableMapAccessor extends AbstractStyleableMapAccessor<Point2D> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, Point2D> cssMetaData;
    @NonNull
    private final NonNullMapAccessor<Double> xKey;
    @NonNull
    private final NonNullMapAccessor<Double> yKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     */
    public Point2DStyleableMapAccessor(String name, @NonNull NonNullMapAccessor<Double> xKey, @NonNull NonNullMapAccessor<Double> yKey) {
        this(name, xKey, yKey, new Point2DConverter(false));
    }

    /**
     * Creates a new instance with the specified name.
     *
     * @param name      the name of the accessor
     * @param xKey      the key for the x coordinate of the point
     * @param yKey      the key for the y coordinate of the point
     * @param converter String converter for the point
     */
    public Point2DStyleableMapAccessor(String name, @NonNull NonNullMapAccessor<Double> xKey, @NonNull NonNullMapAccessor<Double> yKey, Converter<Point2D> converter) {
        super(name, Point2D.class, new MapAccessor<?>[]{xKey, yKey}, new Point2D(xKey.getDefaultValueNonNull(), yKey.getDefaultValueNonNull()));

        Function<Styleable, StyleableProperty<Point2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        this.converter = converter;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        CssMetaData<Styleable, Point2D> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), getDefaultValue(), inherits);
        cssMetaData = md;

        this.xKey = xKey;
        this.yKey = yKey;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, Point2D> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<Point2D> converter;

    @Override
    public final @NonNull Converter<Point2D> getCssConverter() {
        return converter;
    }

    @NonNull
    @Override
    public Point2D get(@NonNull Map<? super Key<?>, Object> a) {
        return new Point2D(xKey.getNonNull(a), yKey.getNonNull(a));
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @Nullable Point2D value) {
        Objects.requireNonNull(value, "value is null");
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
