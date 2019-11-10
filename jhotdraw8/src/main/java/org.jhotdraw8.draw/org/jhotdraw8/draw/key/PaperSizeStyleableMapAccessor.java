/*
 * @(#)PaperSizeStyleableMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssPoint2D;
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
public class PaperSizeStyleableMapAccessor extends AbstractStyleableMapAccessor<CssPoint2D> {

    private final static long serialVersionUID = 1L;
    private Converter<CssPoint2D> converter;

    @NonNull
    private final CssMetaData<?, CssPoint2D> cssMetaData;
    @NonNull
    private final MapAccessor<CssSize> xKey;
    @NonNull
    private final MapAccessor<CssSize> yKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     */
    public PaperSizeStyleableMapAccessor(String name, @NonNull MapAccessor<CssSize> xKey, @NonNull MapAccessor<CssSize> yKey) {
        super(name, CssPoint2D.class, new MapAccessor<?>[]{xKey, yKey}, new CssPoint2D(xKey.getDefaultValue(), yKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<CssPoint2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, CssPoint2D> cnvrtr
                = new StyleConverterAdapter<>(getConverter());
        CssMetaData<Styleable, CssPoint2D> md
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
    public Converter<CssPoint2D> getConverter() {
        if (converter == null) {
            converter = new CssPaperSizeConverter();
        }
        return converter;
    }

    @NonNull
    @Override
    public CssMetaData<?, CssPoint2D> getCssMetaData() {
        return cssMetaData;

    }

    @NonNull
    @Override
    public CssPoint2D put(@NonNull Map<? super Key<?>, Object> a, @NonNull CssPoint2D value) {
        CssPoint2D oldValue = get(a);
        xKey.put(a, value.getX());
        yKey.put(a, value.getY());
        return oldValue;
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
