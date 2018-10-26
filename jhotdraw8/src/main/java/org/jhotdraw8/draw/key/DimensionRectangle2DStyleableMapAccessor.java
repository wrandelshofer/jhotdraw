/* @(#)Rectangle2DStyleableMapAccessor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Rectangle2D;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.text.CssDimension;
import org.jhotdraw8.css.text.CssRectangle2DConverter;
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
public class DimensionRectangle2DStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<Rectangle2D> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, Rectangle2D> cssMetaData;
    @Nonnull
    private final MapAccessor<CssDimension> xKey;
    @Nonnull
    private final MapAccessor<CssDimension> yKey;
    @Nonnull
    private final MapAccessor<CssDimension> widthKey;
    @Nonnull
    private final MapAccessor<CssDimension> heightKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the rectangle
     * @param yKey the key for the y coordinate of the rectangle
     * @param widthKey the key for the width of the rectangle
     * @param heightKey the key for the height of the rectangle
     */
    public DimensionRectangle2DStyleableMapAccessor(String name, MapAccessor<CssDimension> xKey, MapAccessor<CssDimension> yKey, MapAccessor<CssDimension> widthKey, MapAccessor<CssDimension> heightKey) {
        super(name, Rectangle2D.class, new MapAccessor<?>[]{xKey, yKey, widthKey, heightKey}, new Rectangle2D(xKey.getDefaultValue().getConvertedValue(),
                yKey.getDefaultValue().getConvertedValue(), widthKey.getDefaultValue().getConvertedValue(), heightKey.getDefaultValue().getConvertedValue()));

        Function<Styleable, StyleableProperty<Rectangle2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        CssMetaData<Styleable, Rectangle2D> md
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
    public CssMetaData<?, Rectangle2D> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<Rectangle2D> converter= new CssRectangle2DConverter(false);

    @Override
    public Converter<Rectangle2D> getConverter() {
        return converter;
    }

    @Nonnull
    @Override
    public Rectangle2D get(Map<? super Key<?>, Object> a) {
        return new Rectangle2D(xKey.get(a).getConvertedValue(),
                yKey.get(a).getConvertedValue(),
                max(0.0,widthKey.get(a).getConvertedValue()),
                max(0.0,heightKey.get(a).getConvertedValue()));
    }

    @Nonnull
    @Override
    public Rectangle2D put(Map<? super Key<?>, Object> a, @Nonnull Rectangle2D value) {
        Rectangle2D oldValue = get(a);
        xKey.put(a, new CssDimension(value.getMinX(),null));
        yKey.put(a, new CssDimension(value.getMinY(),null));
        widthKey.put(a, new CssDimension(value.getWidth(),null));
        heightKey.put(a, new CssDimension(value.getHeight(),null));
        return oldValue;
    }

    @Nonnull
    @Override
    public Rectangle2D remove(Map<? super Key<?>, Object> a) {
        Rectangle2D oldValue = get(a);
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
