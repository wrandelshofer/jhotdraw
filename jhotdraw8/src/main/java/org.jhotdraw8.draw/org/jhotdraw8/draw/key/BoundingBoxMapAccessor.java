/*
 * @(#)BoundingBoxMapAccessor.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.geometry.BoundingBox;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;

import java.util.Map;

/**
 * BoundingBoxMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class BoundingBoxMapAccessor extends AbstractMapAccessor<BoundingBox> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final MapAccessor<Double> xKey;
    @NonNull
    private final MapAccessor<Double> yKey;
    @NonNull
    private final MapAccessor<Double> widthKey;
    @NonNull
    private final MapAccessor<Double> heightKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name      the name of the accessor
     * @param xKey      the key for the x coordinate of the rectangle
     * @param yKey      the key for the y coordinate of the rectangle
     * @param widthKey  the key for the width of the rectangle
     * @param heightKey the key for the height of the rectangle
     */
    public BoundingBoxMapAccessor(String name, @NonNull MapAccessor<Double> xKey, @NonNull MapAccessor<Double> yKey, @NonNull MapAccessor<Double> widthKey, @NonNull MapAccessor<Double> heightKey) {
        super(name, BoundingBox.class, new MapAccessor<?>[]{xKey, yKey, widthKey, heightKey}, new BoundingBox(xKey.getDefaultValue(), yKey.getDefaultValue(), widthKey.getDefaultValue(), heightKey.getDefaultValue()));

        this.xKey = xKey;
        this.yKey = yKey;
        this.widthKey = widthKey;
        this.heightKey = heightKey;
    }

    @NonNull
    @Override
    public BoundingBox get(@NonNull Map<? super Key<?>, Object> a) {
        return new BoundingBox(xKey.get(a), yKey.get(a), widthKey.get(a), heightKey.get(a));
    }

    @NonNull
    @Override
    public BoundingBox put(@NonNull Map<? super Key<?>, Object> a, @Nullable BoundingBox value) {
        BoundingBox oldValue = get(a);
        xKey.put(a, value.getMinX());
        yKey.put(a, value.getMinY());
        widthKey.put(a, value.getWidth());
        heightKey.put(a, value.getHeight());
        return oldValue;
    }

    @NonNull
    @Override
    public BoundingBox remove(@NonNull Map<? super Key<?>, Object> a) {
        BoundingBox oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        widthKey.remove(a);
        heightKey.remove(a);
        return oldValue;
    }
}
