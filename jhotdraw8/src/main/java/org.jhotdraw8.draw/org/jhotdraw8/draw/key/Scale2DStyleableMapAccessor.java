/*
 * @(#)Scale2DStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.text.CssScale2DConverter;
import org.jhotdraw8.text.Converter;

import java.util.Map;

/**
 * Scale2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class Scale2DStyleableMapAccessor extends AbstractStyleableMapAccessor<Point2D> {

    private static final long serialVersionUID = 1L;

    private final @NonNull MapAccessor<Double> xKey;
    private final @NonNull MapAccessor<Double> yKey;
    private Converter<Point2D> converter = new CssScale2DConverter();

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     */
    public Scale2DStyleableMapAccessor(String name, @NonNull MapAccessor<Double> xKey, @NonNull MapAccessor<Double> yKey) {
        super(name, Point2D.class, new MapAccessor<?>[]{xKey, yKey}, new Point2D(xKey.getDefaultValue(), yKey.getDefaultValue()));

        this.xKey = xKey;
        this.yKey = yKey;
    }

    @Override
    public @NonNull Point2D get(@NonNull Map<? super Key<?>, Object> a) {

        Double x = xKey.get(a);
        Double y = yKey.get(a);
        return new Point2D(x == null ? 0 : x, y == null ? 0 : y);
    }

    @Override
    public @NonNull Converter<Point2D> getCssConverter() {
        return converter;
    }

    @Override
    public @NonNull Point2D remove(@NonNull Map<? super Key<?>, Object> a) {
        Point2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        return oldValue;
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @NonNull Point2D value) {
        xKey.put(a, value.getX());
        yKey.put(a, value.getY());
    }
}
