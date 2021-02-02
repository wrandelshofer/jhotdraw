/*
 * @(#)SymmetricCssPoint2DStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssSymmetricPoint2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * SymmetricCssPoint2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class SymmetricCssPoint2DStyleableMapAccessor
        extends AbstractStyleableMapAccessor<@NonNull CssPoint2D>
        implements NonNullMapAccessor<@NonNull CssPoint2D> {

    private final static long serialVersionUID = 1L;
    @NonNull
    private Converter<@NonNull CssPoint2D> converter;

    @NonNull
    private final CssMetaData<?, @NonNull CssPoint2D> cssMetaData;
    @NonNull
    private final NonNullMapAccessor<@NonNull CssSize> xKey;
    @NonNull
    private final NonNullMapAccessor<@NonNull CssSize> yKey;

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

        Function<Styleable, StyleableProperty<CssPoint2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, CssPoint2D> cnvrtr
                = new StyleConverterAdapter<>(getCssConverter());
        CssMetaData<@NonNull Styleable, @NonNull CssPoint2D> md
                = new SimpleCssMetaData<>(property, function,
                cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.xKey = xKey;
        this.yKey = yKey;
    }

    @NonNull
    @Override
    public CssPoint2D get(@NonNull Map<? super Key<?>, Object> a) {
        return new CssPoint2D(xKey.get(a), yKey.get(a));
    }


    @Override
    public @NonNull Converter<CssPoint2D> getCssConverter() {
        if (converter == null) {
            converter = new CssSymmetricPoint2DConverter();
        }
        return converter;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, CssPoint2D> getCssMetaData() {
        return cssMetaData;

    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @Nullable CssPoint2D value) {
        Objects.requireNonNull(value, "value is null");
        xKey.put(a, value.getX());
        yKey.put(a, value.getY());
    }

    @NonNull
    @Override
    public CssPoint2D remove(@NonNull Map<? super Key<?>, Object> a) {
        CssPoint2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        return oldValue;
    }

}
