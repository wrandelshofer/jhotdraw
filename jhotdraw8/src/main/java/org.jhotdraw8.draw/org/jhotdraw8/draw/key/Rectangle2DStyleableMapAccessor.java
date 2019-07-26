/*
 * @(#)Rectangle2DStyleableMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Rectangle2D;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.text.Rectangle2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.function.Function;

import static java.lang.Double.max;

/**
 * Rectangle2DStyleableNonnullMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Rectangle2DStyleableMapAccessor extends AbstractStyleableMapAccessor<Rectangle2D>
        implements NonnullMapAccessor<Rectangle2D> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, Rectangle2D> cssMetaData;
    @Nonnull
    private final NonnullMapAccessor<Double> xKey;
    @Nonnull
    private final NonnullMapAccessor<Double> yKey;
    @Nonnull
    private final NonnullMapAccessor<Double> widthKey;
    @Nonnull
    private final NonnullMapAccessor<Double> heightKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name      the name of the accessor
     * @param xKey      the key for the x coordinate of the rectangle
     * @param yKey      the key for the y coordinate of the rectangle
     * @param widthKey  the key for the width of the rectangle
     * @param heightKey the key for the height of the rectangle
     */
    public Rectangle2DStyleableMapAccessor(@Nonnull String name,
                                           @Nonnull NonnullMapAccessor<Double> xKey,
                                           @Nonnull NonnullMapAccessor<Double> yKey,
                                           @Nonnull NonnullMapAccessor<Double> widthKey,
                                           @Nonnull NonnullMapAccessor<Double> heightKey) {
        super(name, Rectangle2D.class, new NonnullMapAccessor<?>[]{xKey, yKey, widthKey, heightKey}, new Rectangle2D(xKey.getDefaultValueNonnull(), yKey.getDefaultValueNonnull(),
                widthKey.getDefaultValueNonnull(), heightKey.getDefaultValueNonnull()));

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

    @Nonnull
    @Override
    public CssMetaData<?, Rectangle2D> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<Rectangle2D> converter = new Rectangle2DConverter(false);

    @Override
    public Converter<Rectangle2D> getConverter() {
        return converter;
    }

    @Nonnull
    @Override
    public Rectangle2D get(@Nonnull Map<? super Key<?>, Object> a) {
        return new Rectangle2D(xKey.getNonnull(a), yKey.getNonnull(a), max(0.0, widthKey.getNonnull(a)), max(0.0, heightKey.getNonnull(a)));
    }

    @Nonnull
    @Override
    public Rectangle2D put(@Nonnull Map<? super Key<?>, Object> a, @Nullable Rectangle2D value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        Rectangle2D oldValue = get(a);
        xKey.put(a, value.getMinX());
        yKey.put(a, value.getMinY());
        widthKey.put(a, value.getWidth());
        heightKey.put(a, value.getHeight());
        return oldValue;
    }

    @Nonnull
    @Override
    public Rectangle2D remove(@Nonnull Map<? super Key<?>, Object> a) {
        Rectangle2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        widthKey.remove(a);
        heightKey.remove(a);
        return oldValue;
    }
}
