/*
 * @(#)CssPoint2DStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssPoint2DConverter;
import org.jhotdraw8.text.Converter;

import java.util.Map;

/**
 * CssPoint2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class CssPoint2DStyleableMapAccessor
        extends AbstractStyleableMapAccessor<@NonNull CssPoint2D>
        implements NonNullMapAccessor<@NonNull CssPoint2D> {

    private static final long serialVersionUID = 1L;
    private final Converter<@NonNull CssPoint2D> converter;
    private final @NonNull NonNullMapAccessor<CssSize> xKey;
    private final @NonNull NonNullMapAccessor<CssSize> yKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     */
    public CssPoint2DStyleableMapAccessor(@NonNull String name, @NonNull NonNullMapAccessor<CssSize> xKey, @NonNull NonNullMapAccessor<CssSize> yKey) {
        this(name, xKey, yKey, new CssPoint2DConverter(false));
    }

    /**
     * Creates a new instance with the specified name.
     *
     * @param name      the name of the accessor
     * @param xKey      the key for the x coordinate of the point
     * @param yKey      the key for the y coordinate of the point
     * @param converter String converter for the point
     */
    public CssPoint2DStyleableMapAccessor(@NonNull String name, @NonNull NonNullMapAccessor<CssSize> xKey, @NonNull NonNullMapAccessor<CssSize> yKey, @NonNull Converter<CssPoint2D> converter) {
        super(name, CssPoint2D.class, new NonNullMapAccessor<?>[]{xKey, yKey}, new CssPoint2D(xKey.getDefaultValue(), yKey.getDefaultValue()));

        this.converter = converter;
        this.xKey = xKey;
        this.yKey = yKey;
    }

    @Override
    public @NonNull CssPoint2D get(@NonNull Map<? super Key<?>, Object> a) {
        return new CssPoint2D(xKey.get(a), yKey.get(a));
    }


    @Override
    public @NonNull Converter<@NonNull CssPoint2D> getCssConverter() {
        return converter;
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @NonNull CssPoint2D value) {
        xKey.put(a, value.getX());
        yKey.put(a, value.getY());
    }

    @Override
    public @NonNull CssPoint2D remove(@NonNull Map<? super Key<?>, Object> a) {
        CssPoint2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        return oldValue;
    }

}
