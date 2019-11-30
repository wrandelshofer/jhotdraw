/*
 * @(#)Scale3DStyleableMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Point3D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssScale3DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.function.Function;

/**
 * Scale3DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class Scale3DStyleableMapAccessor extends AbstractStyleableMapAccessor<Point3D>
        implements NonNullMapAccessor<Point3D> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, Point3D> cssMetaData;
    @NonNull
    private final MapAccessor<Double> xKey;
    @NonNull
    private final MapAccessor<Double> yKey;
    @NonNull
    private final MapAccessor<Double> zKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     * @param zKey the key for the u coordinate of the point
     */
    public Scale3DStyleableMapAccessor(String name, @NonNull MapAccessor<Double> xKey, @NonNull MapAccessor<Double> yKey, @NonNull MapAccessor<Double> zKey) {
        this(name, xKey, yKey, zKey, new CssScale3DConverter(false));
    }

    /**
     * Creates a new instance with the specified name.
     *
     * @param name      the name of the accessor
     * @param xKey      the key for the x coordinate of the point
     * @param yKey      the key for the y coordinate of the point
     * @param zKey      the key for the u coordinate of the point
     * @param converter String converter for the scale factor with 3 coordinates (x-factor, y-factor, z-factor).
     */
    public Scale3DStyleableMapAccessor(String name, @NonNull MapAccessor<Double> xKey, @NonNull MapAccessor<Double> yKey, @NonNull MapAccessor<Double> zKey, Converter<Point3D> converter) {
        super(name, Point3D.class, new MapAccessor<?>[]{xKey, yKey, zKey}, new Point3D(xKey.getDefaultValue(), yKey.getDefaultValue(), zKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<Point3D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Point3D> cnvrtr
                = new StyleConverterAdapter<>(converter);
        CssMetaData<Styleable, Point3D> md
                = new SimpleCssMetaData<>(property, function,
                cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.converter = converter;

        this.xKey = xKey;
        this.yKey = yKey;
        this.zKey = zKey;
    }

    @NonNull
    @Override
    public CssMetaData<?, Point3D> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<Point3D> converter;

    @Override
    public Converter<Point3D> getConverter() {
        return converter;
    }

    @NonNull
    @Override
    public Point3D get(@NonNull Map<? super Key<?>, Object> a) {
        return new Point3D(xKey.get(a), yKey.get(a), zKey.get(a));
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @NonNull Point3D value) {
        xKey.put(a, value.getX());
        yKey.put(a, value.getY());
        zKey.put(a, value.getZ());
    }

    @NonNull
    @Override
    public Point3D remove(@NonNull Map<? super Key<?>, Object> a) {
        Point3D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        zKey.remove(a);
        return oldValue;
    }

}
