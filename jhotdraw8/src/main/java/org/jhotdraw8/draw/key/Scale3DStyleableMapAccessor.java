/* @(#)Scale3DStyleableMapAccessor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.Map;
import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Point3D;
import javax.annotation.Nonnull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssScale3DConverter;
import org.jhotdraw8.text.StyleConverterAdapter;

/**
 * Scale3DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Scale3DStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<Point3D> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, Point3D> cssMetaData;
    @Nonnull
    private final MapAccessor<Double> xKey;
    @Nonnull
    private final MapAccessor<Double> yKey;
    @Nonnull
    private final MapAccessor<Double> zKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     * @param zKey the key for the u coordinate of the point
     */
    public Scale3DStyleableMapAccessor(String name, MapAccessor<Double> xKey, MapAccessor<Double> yKey, MapAccessor<Double> zKey) {
        super(name, Point3D.class, new MapAccessor<?>[]{xKey, yKey, zKey}, new Point3D(xKey.getDefaultValue(), yKey.getDefaultValue(), zKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<Point3D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Point3D> cnvrtr
                = new StyleConverterAdapter<>(getConverter());
        CssMetaData<Styleable, Point3D> md
                = new SimpleCssMetaData<>(property, function,
                        cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.xKey = xKey;
        this.yKey = yKey;
        this.zKey = zKey;
    }

    @Nonnull
    @Override
    public CssMetaData<?, Point3D> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<Point3D> converter;

    @Override
    public Converter<Point3D> getConverter() {
        if (converter == null) {
            converter = new CssScale3DConverter();
        }
        return converter;
    }

    @Nonnull
    @Override
    public Point3D get(Map<? super Key<?>, Object> a) {
        return new Point3D(xKey.get(a), yKey.get(a), zKey.get(a));
    }

    @Nonnull
    @Override
    public Point3D put(Map<? super Key<?>, Object> a, @Nonnull Point3D value) {
        Point3D oldValue = get(a);
        xKey.put(a, value.getX());
        yKey.put(a, value.getY());
        zKey.put(a, value.getZ());
        return oldValue;
    }

    @Nonnull
    @Override
    public Point3D remove(Map<? super Key<?>, Object> a) {
        Point3D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        zKey.remove(a);
        return oldValue;
    }

    @Override
    public boolean isNullable() {
        return false;
    }
}
