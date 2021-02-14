/*
 * @(#)Rectangle2DStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Rectangle2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.Rectangle2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.lang.Double.max;

/**
 * Rectangle2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class Rectangle2DStyleableMapAccessor extends AbstractStyleableMapAccessor<@NonNull Rectangle2D>
        implements NonNullMapAccessor<@NonNull Rectangle2D> {

    private static final long serialVersionUID = 1L;

    private final @NonNull CssMetaData<?, Rectangle2D> cssMetaData;
    private final @NonNull NonNullMapAccessor<Double> xKey;
    private final @NonNull NonNullMapAccessor<Double> yKey;
    private final @NonNull NonNullMapAccessor<Double> widthKey;
    private final @NonNull NonNullMapAccessor<Double> heightKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name      the name of the accessor
     * @param xKey      the key for the x coordinate of the rectangle
     * @param yKey      the key for the y coordinate of the rectangle
     * @param widthKey  the key for the width of the rectangle
     * @param heightKey the key for the height of the rectangle
     */
    public Rectangle2DStyleableMapAccessor(@NonNull String name,
                                           @NonNull NonNullMapAccessor<Double> xKey,
                                           @NonNull NonNullMapAccessor<Double> yKey,
                                           @NonNull NonNullMapAccessor<Double> widthKey,
                                           @NonNull NonNullMapAccessor<Double> heightKey) {
        super(name, Rectangle2D.class, new NonNullMapAccessor<?>[]{xKey, yKey, widthKey, heightKey}, new Rectangle2D(xKey.getDefaultValueNonNull(), yKey.getDefaultValueNonNull(),
                widthKey.getDefaultValueNonNull(), heightKey.getDefaultValueNonNull()));

        Function<Styleable, StyleableProperty<Rectangle2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        cssMetaData = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), getDefaultValue(), inherits);

        this.xKey = xKey;
        this.yKey = yKey;
        this.widthKey = widthKey;
        this.heightKey = heightKey;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, Rectangle2D> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<Rectangle2D> converter = new Rectangle2DConverter(false);

    @Override
    public @NonNull Converter<Rectangle2D> getCssConverter() {
        return converter;
    }

    @Override
    public @NonNull Rectangle2D get(@NonNull Map<? super Key<?>, Object> a) {
        return new Rectangle2D(xKey.getNonNull(a), yKey.getNonNull(a), max(0.0, widthKey.getNonNull(a)), max(0.0, heightKey.getNonNull(a)));
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @Nullable Rectangle2D value) {
        Objects.requireNonNull(value, "value is null");
        xKey.put(a, value.getMinX());
        yKey.put(a, value.getMinY());
        widthKey.put(a, value.getWidth());
        heightKey.put(a, value.getHeight());
    }

    @Override
    public @NonNull Rectangle2D remove(@NonNull Map<? super Key<?>, Object> a) {
        Rectangle2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        widthKey.remove(a);
        heightKey.remove(a);
        return oldValue;
    }
}
