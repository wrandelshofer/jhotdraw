/*
 * @(#)PaperSizeStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssDimension2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssPaperSizeConverter;
import org.jhotdraw8.text.Converter;

import java.util.Map;

/**
 * CssSize2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class PaperSizeStyleableMapAccessor extends AbstractStyleableMapAccessor<CssDimension2D> {

    private static final long serialVersionUID = 1L;
    private Converter<CssDimension2D> converter = new CssPaperSizeConverter();

    private final @NonNull NonNullMapAccessor<CssSize> widthKey;
    private final @NonNull NonNullMapAccessor<CssSize> heightKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param widthKey the key for the x coordinate of the point
     * @param heightKey the key for the y coordinate of the point
     */
    public PaperSizeStyleableMapAccessor(String name, @NonNull NonNullMapAccessor<CssSize> widthKey, @NonNull NonNullMapAccessor<CssSize> heightKey) {
        super(name, CssDimension2D.class, new MapAccessor<?>[]{widthKey, heightKey}, new CssDimension2D(widthKey.getDefaultValue(), heightKey.getDefaultValue()));

        this.widthKey = widthKey;
        this.heightKey = heightKey;
    }

    @Override
    public @NonNull CssDimension2D get(@NonNull Map<? super Key<?>, Object> a) {
        return new CssDimension2D(widthKey.getNonNull(a), heightKey.getNonNull(a));
    }


    @Override
    public @NonNull Converter<CssDimension2D> getCssConverter() {
        return converter;
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @Nullable CssDimension2D value) {
        if (value == null) {
            widthKey.remove(a);
            heightKey.remove(a);
        } else {
            widthKey.put(a, value.getWidth());
            heightKey.put(a, value.getHeight());
        }
    }

    @Override
    public @NonNull CssDimension2D remove(@NonNull Map<? super Key<?>, Object> a) {
        CssDimension2D oldValue = get(a);
        widthKey.remove(a);
        heightKey.remove(a);
        return oldValue;
    }

}
