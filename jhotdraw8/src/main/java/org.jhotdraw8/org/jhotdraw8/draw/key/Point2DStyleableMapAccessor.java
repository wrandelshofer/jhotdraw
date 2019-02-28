/* @(#)Point2DStyleableMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.text.Point2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.function.Function;

/**
 * Point2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Point2DStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<Point2D> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, Point2D> cssMetaData;
    @Nonnull
    private final NonnullMapAccessor<Double> xKey;
    @Nonnull
    private final NonnullMapAccessor<Double> yKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     */
    public Point2DStyleableMapAccessor(String name, NonnullMapAccessor<Double> xKey, NonnullMapAccessor<Double> yKey) {
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
    public Point2DStyleableMapAccessor(String name, NonnullMapAccessor<Double> xKey, NonnullMapAccessor<Double> yKey, Converter<Point2D> converter) {
        super(name, Point2D.class, new MapAccessor<?>[]{xKey, yKey}, new Point2D(xKey.getDefaultValueNonnull(), yKey.getDefaultValueNonnull()));

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

    @Nonnull
    @Override
    public CssMetaData<?, Point2D> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<Point2D> converter;

    @Override
    public final Converter<Point2D> getConverter() {
        return converter;
    }

    @Nonnull
    @Override
    public Point2D get(@Nonnull Map<? super Key<?>, Object> a) {
        return new Point2D(xKey.getNonnull(a), yKey.getNonnull(a));
    }

    @Nonnull
    @Override
    public Point2D put(@Nonnull Map<? super Key<?>, Object> a, @Nullable Point2D value) {
        if (value == null) {
            throw new IllegalArgumentException("value must be nonnull");
        }
        Point2D oldValue = get(a);
        xKey.put(a, value.getX());
        yKey.put(a, value.getY());
        return oldValue;
    }

    @Nonnull
    @Override
    public Point2D remove(@Nonnull Map<? super Key<?>, Object> a) {
        Point2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        return oldValue;
    }

    @Override
    public boolean isNullable() {
        return false;
    }
}
