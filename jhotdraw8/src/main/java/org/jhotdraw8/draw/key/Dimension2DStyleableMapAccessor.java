/* @(#)CssSize2DStyleableMapAccessor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.Map;
import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javax.annotation.Nonnull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.text.CssDimension;
import org.jhotdraw8.css.text.CssDimension2D;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssSize2DConverter;
import org.jhotdraw8.text.StyleConverterAdapter;

/**
 * CssSize2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Dimension2DStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<CssDimension2D> {

    private final static long serialVersionUID = 1L;
    private Converter<CssDimension2D> converter;

    @Nonnull
    private final CssMetaData<?, CssDimension2D> cssMetaData;
    @Nonnull
    private final MapAccessor<CssDimension> xKey;
    @Nonnull
    private final MapAccessor<CssDimension> yKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     */
    public Dimension2DStyleableMapAccessor(String name, MapAccessor<CssDimension> xKey, MapAccessor<CssDimension> yKey) {
        super(name, CssDimension2D.class, new MapAccessor<?>[]{xKey, yKey}, new CssDimension2D(xKey.getDefaultValue(), yKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<CssDimension2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, CssDimension2D> cnvrtr
                = new StyleConverterAdapter<>(getConverter());
        CssMetaData<Styleable, CssDimension2D> md
                = new SimpleCssMetaData<>(property, function,
                        cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.xKey = xKey;
        this.yKey = yKey;
    }
    @Nonnull
    @Override
    public CssDimension2D get(Map<? super Key<?>, Object> a) {
      return new CssDimension2D(xKey.get(a), yKey.get(a));
    }


    @Override
    public Converter<CssDimension2D> getConverter() {
        if (converter == null) {
            converter = new CssSize2DConverter();
        }
        return converter;
    }
    @Nonnull
    @Override
    public CssMetaData<?, CssDimension2D> getCssMetaData() {
      return cssMetaData;
      
    }
    @Override
    public boolean isNullable() {
      return false;
    }

    @Nonnull
    @Override
    public CssDimension2D put(Map<? super Key<?>, Object> a, @Nonnull CssDimension2D value) {
        CssDimension2D oldValue = get(a);
        xKey.put(a, value.getX());
        yKey.put(a, value.getY());
        return oldValue;
    }

    @Nonnull
    @Override
    public CssDimension2D remove(Map<? super Key<?>, Object> a) {
        CssDimension2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        return oldValue;
    }

}
