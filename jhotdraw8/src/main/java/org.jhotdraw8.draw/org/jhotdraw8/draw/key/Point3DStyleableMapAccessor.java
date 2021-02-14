/*
 * @(#)Point3DStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.geometry.Point3D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.Point3DConverter;
import org.jhotdraw8.text.Converter;

import java.util.Map;

/**
 * Point3DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class Point3DStyleableMapAccessor extends AbstractStyleableMapAccessor<@NonNull Point3D>
        implements NonNullMapAccessor<@NonNull Point3D> {

    private static final long serialVersionUID = 1L;

    private final @NonNull MapAccessor<Double> xKey;
    private final @NonNull MapAccessor<Double> yKey;
    private final @NonNull MapAccessor<Double> zKey;
    private final Converter<Point3D> converter;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     * @param zKey the key for the u coordinate of the point
     */
    public Point3DStyleableMapAccessor(String name, @NonNull MapAccessor<Double> xKey, @NonNull MapAccessor<Double> yKey, @NonNull MapAccessor<Double> zKey) {
        this(name, xKey, yKey, zKey, new Point3DConverter(false));
    }

    public Point3DStyleableMapAccessor(String name, @NonNull MapAccessor<Double> xKey, @NonNull MapAccessor<Double> yKey, @NonNull MapAccessor<Double> zKey, Converter<Point3D> converter) {
        super(name, Point3D.class, new MapAccessor<?>[]{xKey, yKey, zKey}, new Point3D(xKey.getDefaultValue(), yKey.getDefaultValue(), zKey.getDefaultValue()));
        this.converter = converter;
        this.xKey = xKey;
        this.yKey = yKey;
        this.zKey = zKey;
    }

    @Override
    public @NonNull Converter<Point3D> getCssConverter() {
        return converter;
    }

    @Override
    public @NonNull Point3D get(@NonNull Map<? super Key<?>, Object> a) {
        Double x = xKey.get(a);
        Double y = yKey.get(a);
        Double z = zKey.get(a);
        return new Point3D(x == null ? 0 : x, y == null ? 0 : y, z == null ? 0 : z);
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @Nullable Point3D value) {
        if (value == null) {
            xKey.remove(a);
            yKey.remove(a);
            zKey.remove(a);
        } else {
            xKey.put(a, value.getX());
            yKey.put(a, value.getY());
            zKey.put(a, value.getZ());
        }
    }

    @Override
    public @NonNull Point3D remove(@NonNull Map<? super Key<?>, Object> a) {
        Point3D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        zKey.remove(a);
        return oldValue;
    }

}
