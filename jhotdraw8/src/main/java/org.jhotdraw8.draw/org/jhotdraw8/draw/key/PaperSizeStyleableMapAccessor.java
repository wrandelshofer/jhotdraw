/*
 * @(#)PaperSizeStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssDimension2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssPaperSizeConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.function.Function;

/**
 * CssSize2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class PaperSizeStyleableMapAccessor extends AbstractStyleableMapAccessor<CssDimension2D> {

    private static final long serialVersionUID = 1L;
    private Converter<CssDimension2D> converter;

    private final @NonNull CssMetaData<?, CssDimension2D> cssMetaData;
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

        Function<Styleable, StyleableProperty<CssDimension2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, CssDimension2D> cnvrtr
                = new StyleConverterAdapter<>(getCssConverter());
        CssMetaData<Styleable, CssDimension2D> md
                = new SimpleCssMetaData<>(property, function,
                cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.widthKey = widthKey;
        this.heightKey = heightKey;
    }

    @Override
    public @NonNull CssDimension2D get(@NonNull Map<? super Key<?>, Object> a) {
        return new CssDimension2D(widthKey.getNonNull(a), heightKey.getNonNull(a));
    }


    @Override
    public @NonNull Converter<CssDimension2D> getCssConverter() {
        if (converter == null) {
            converter = new CssPaperSizeConverter();
        }
        return converter;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, CssDimension2D> getCssMetaData() {
        return cssMetaData;

    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @NonNull CssDimension2D value) {
        widthKey.put(a, value.getWidth());
        heightKey.put(a, value.getHeight());
    }

    @Override
    public @NonNull CssDimension2D remove(@NonNull Map<? super Key<?>, Object> a) {
        CssDimension2D oldValue = get(a);
        widthKey.remove(a);
        heightKey.remove(a);
        return oldValue;
    }

}
