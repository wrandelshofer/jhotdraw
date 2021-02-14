/*
 * @(#)SymmetricCssPoint2DStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssSymmetricPoint2DConverter;
import org.jhotdraw8.text.Converter;

import java.util.Map;
import java.util.Objects;

/**
 * SymmetricCssPoint2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class SymmetricCssPoint2DStyleableMapAccessor
        extends AbstractStyleableMapAccessor<@NonNull CssPoint2D>
        implements NonNullMapAccessor<@NonNull CssPoint2D> {

    private static final long serialVersionUID = 1L;
    private @NonNull Converter<@NonNull CssPoint2D> converter = new CssSymmetricPoint2DConverter();

    private final @NonNull NonNullMapAccessor<@NonNull CssSize> xKey;
    private final @NonNull NonNullMapAccessor<@NonNull CssSize> yKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     */
    public SymmetricCssPoint2DStyleableMapAccessor(@NonNull String name,
                                                   @NonNull NonNullMapAccessor<@NonNull CssSize> xKey,
                                                   @NonNull NonNullMapAccessor<@NonNull CssSize> yKey) {
        super(name, CssPoint2D.class, new NonNullMapAccessor<?>[]{xKey, yKey}, new CssPoint2D(xKey.getDefaultValue(), yKey.getDefaultValue()));

        this.xKey = xKey;
        this.yKey = yKey;
    }

    @Override
    public @NonNull CssPoint2D get(@NonNull Map<? super Key<?>, Object> a) {
        return new CssPoint2D(xKey.get(a), yKey.get(a));
    }


    @Override
    public @NonNull Converter<CssPoint2D> getCssConverter() {
        return converter;
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @Nullable CssPoint2D value) {
        Objects.requireNonNull(value, "value is null");
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
