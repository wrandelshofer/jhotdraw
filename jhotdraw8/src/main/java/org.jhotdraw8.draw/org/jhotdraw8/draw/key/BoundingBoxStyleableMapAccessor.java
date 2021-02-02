/*
 * @(#)BoundingBoxStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.BoundingBox;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.text.CssBoundingBoxConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * BoundingBoxStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class BoundingBoxStyleableMapAccessor extends AbstractStyleableMapAccessor<BoundingBox> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, BoundingBox> cssMetaData;
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
    public BoundingBoxStyleableMapAccessor(String name, @NonNull MapAccessor<Double> xKey, @NonNull MapAccessor<Double> yKey, @NonNull MapAccessor<Double> widthKey, @NonNull MapAccessor<Double> heightKey) {
        super(name, BoundingBox.class, new MapAccessor<?>[]{xKey, yKey, widthKey, heightKey}, new BoundingBox(xKey.getDefaultValue(), yKey.getDefaultValue(), widthKey.getDefaultValue(), heightKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<BoundingBox>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, BoundingBox> cnvrtr
                = new StyleConverterAdapter<>(getCssConverter());
        CssMetaData<Styleable, BoundingBox> md
                = new SimpleCssMetaData<>(property, function,
                cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.xKey = xKey;
        this.yKey = yKey;
        this.widthKey = widthKey;
        this.heightKey = heightKey;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, BoundingBox> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<BoundingBox> converter;

    @Override
    public @NonNull Converter<BoundingBox> getCssConverter() {
        if (converter == null) {
            converter = new CssBoundingBoxConverter(false);
        }
        return converter;
    }

    @NonNull
    @Override
    public BoundingBox get(@NonNull Map<? super Key<?>, Object> a) {
        return new BoundingBox(xKey.get(a), yKey.get(a), widthKey.get(a), heightKey.get(a));
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @Nullable BoundingBox value) {
        Objects.requireNonNull(value, "value is null");
        xKey.put(a, value.getMinX());
        yKey.put(a, value.getMinY());
        widthKey.put(a, value.getWidth());
        heightKey.put(a, value.getHeight());
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
