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
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssSize;
import org.jhotdraw8.text.CssSize2D;
import org.jhotdraw8.text.CssSize2DConverter;
import org.jhotdraw8.text.StyleConverterAdapter;

/**
 * CssSize2DStyleableMapAccessor.
 *
 * @author werni
 */
public class Size2DStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<CssSize2D> {

    private final static long serialVersionUID = 1L;
    private Converter<CssSize2D> converter;

    private final CssMetaData<?, CssSize2D> cssMetaData;
    private final MapAccessor<CssSize> xKey;
    private final MapAccessor<CssSize> yKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param xKey the key for the x coordinate of the point
     * @param yKey the key for the y coordinate of the point
     */
    public Size2DStyleableMapAccessor(String name, MapAccessor<CssSize> xKey, MapAccessor<CssSize> yKey) {
        super(name, CssSize2D.class, new MapAccessor<?>[]{xKey, yKey}, new CssSize2D(xKey.getDefaultValue(), yKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<CssSize2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, CssSize2D> cnvrtr
                = new StyleConverterAdapter<>(getConverter());
        CssMetaData<Styleable, CssSize2D> md
                = new SimpleCssMetaData<>(property, function,
                        cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.xKey = xKey;
        this.yKey = yKey;
    }
    @Override
    public CssSize2D get(Map<? super Key<?>, Object> a) {
      return new CssSize2D(xKey.get(a), yKey.get(a));
    }


    @Override
    public Converter<CssSize2D> getConverter() {
        if (converter == null) {
            converter = new CssSize2DConverter();
        }
        return converter;
    }
    @Override
    public CssMetaData<?, CssSize2D> getCssMetaData() {
      return cssMetaData;
      
    }
    @Override
    public boolean isNullable() {
      return false;
    }

    @Override
    public CssSize2D put(Map<? super Key<?>, Object> a, CssSize2D value) {
        CssSize2D oldValue = get(a);
        xKey.put(a, value.getX());
        yKey.put(a, value.getY());
        return oldValue;
    }

    @Override
    public CssSize2D remove(Map<? super Key<?>, Object> a) {
        CssSize2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        return oldValue;
    }

}
