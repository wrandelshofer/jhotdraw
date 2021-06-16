/*
 * @(#)CssRectangle2DStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssRectangle2DConverter;
import org.jhotdraw8.text.Converter;

import java.util.Map;

/**
 * Rectangle2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class NullableCssRectangle2DStyleableMapAccessor extends AbstractStyleableMapAccessor<@Nullable CssRectangle2D>
        implements NonNullMapAccessor<@NonNull CssRectangle2D> {

    private static final long serialVersionUID = 1L;

    private final @NonNull MapAccessor<CssSize> xKey;
    private final @NonNull MapAccessor<CssSize> yKey;
    private final @NonNull MapAccessor<CssSize> widthKey;
    private final @NonNull MapAccessor<CssSize> heightKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name      the name of the accessor
     * @param xKey      the key for the x coordinate of the rectangle
     * @param yKey      the key for the y coordinate of the rectangle
     * @param widthKey  the key for the width of the rectangle
     * @param heightKey the key for the height of the rectangle
     */
    public NullableCssRectangle2DStyleableMapAccessor(String name, @NonNull MapAccessor<CssSize> xKey, @NonNull MapAccessor<CssSize> yKey, @NonNull MapAccessor<CssSize> widthKey, @NonNull MapAccessor<CssSize> heightKey) {
        super(name, CssRectangle2D.class, new MapAccessor<?>[]{xKey, yKey, widthKey, heightKey}, null);

        this.xKey = xKey;
        this.yKey = yKey;
        this.widthKey = widthKey;
        this.heightKey = heightKey;
    }

    private final Converter<CssRectangle2D> converter = new CssRectangle2DConverter(true);

    @Override
    public @NonNull Converter<CssRectangle2D> getCssConverter() {
        return converter;
    }

    @Override
    public @Nullable CssRectangle2D get(@NonNull Map<? super Key<?>, Object> a) {
        final CssSize x = xKey.get(a);
        final CssSize y = yKey.get(a);
        final CssSize width = widthKey.get(a);
        final CssSize height = heightKey.get(a);
        return (x == null || y == null || width == null || height == null)
                ? null
                : new CssRectangle2D(x, y, width, height);
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @Nullable CssRectangle2D value) {
        if (value == null) {
            xKey.put(a, null);
            yKey.put(a, null);
            widthKey.put(a, null);
            heightKey.put(a, null);
        } else {
            xKey.put(a, value.getMinX());
            yKey.put(a, value.getMinY());
            widthKey.put(a, value.getWidth());
            heightKey.put(a, value.getHeight());
        }
    }

    @Override
    public @NonNull CssRectangle2D remove(@NonNull Map<? super Key<?>, Object> a) {
        CssRectangle2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        widthKey.remove(a);
        heightKey.remove(a);
        return oldValue;
    }

}
