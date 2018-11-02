/* @(#)Rectangle2DStyleableMapAccessor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssDimensionRectangle2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Function;

import static java.lang.Math.max;

/**
 * Rectangle2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssRectangle2DStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<CssRectangle2D> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, CssRectangle2D> cssMetaData;
    @Nonnull
    private final MapAccessor<CssSize> xKey;
    @Nonnull
    private final MapAccessor<CssSize> yKey;
    @Nonnull
    private final MapAccessor<CssSize> widthKey;
    @Nonnull
    private final MapAccessor<CssSize> heightKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the rectangle
     * @param yKey the key for the y coordinate of the rectangle
     * @param widthKey the key for the width of the rectangle
     * @param heightKey the key for the height of the rectangle
     */
    public CssRectangle2DStyleableMapAccessor(String name, MapAccessor<CssSize> xKey, MapAccessor<CssSize> yKey, MapAccessor<CssSize> widthKey, MapAccessor<CssSize> heightKey) {
        super(name, CssRectangle2D.class, new MapAccessor<?>[]{xKey, yKey, widthKey, heightKey}, new CssRectangle2D(
                xKey.getDefaultValueNonnull(),
                yKey.getDefaultValueNonnull(),
                widthKey.getDefaultValueNonnull(),
                heightKey.getDefaultValueNonnull()));

        Function<Styleable, StyleableProperty<CssRectangle2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        CssMetaData<Styleable, CssRectangle2D> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), getDefaultValue(), inherits);
        cssMetaData = md;

        this.xKey = xKey;
        this.yKey = yKey;
        this.widthKey = widthKey;
        this.heightKey = heightKey;
    }

    @Nonnull
    @Override
    public CssMetaData<?, CssRectangle2D> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<CssRectangle2D> converter= new CssDimensionRectangle2DConverter(false);

    @Override
    public Converter<CssRectangle2D> getConverter() {
        return converter;
    }

    @Nonnull
    @Override
    public CssRectangle2D get(Map<? super Key<?>, Object> a) {
        return new CssRectangle2D(xKey.get(a),
                yKey.get(a),
                widthKey.get(a),
                heightKey.get(a));
    }

    @Nonnull
    @Override
    public CssRectangle2D put(Map<? super Key<?>, Object> a, @Nonnull CssRectangle2D value) {
        CssRectangle2D oldValue = get(a);
        xKey.put(a, value.getMinX());
        yKey.put(a, value.getMinY());
        widthKey.put(a, value.getWidth());
        heightKey.put(a, value.getHeight());
        return oldValue;
    }

    @Nonnull
    @Override
    public CssRectangle2D remove(Map<? super Key<?>, Object> a) {
        CssRectangle2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        widthKey.remove(a);
        heightKey.remove(a);
        return oldValue;
    }

    @Override
    public boolean isNullable() {
        return false;
    }
}
