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
import org.jhotdraw8.css.text.Dimension2D;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssPaperSizeConverter;
import org.jhotdraw8.text.StyleConverterAdapter;

/**
 * CssSize2DStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PaperSizeStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<Dimension2D> {

    private final static long serialVersionUID = 1L;
    private Converter<Dimension2D> converter;

    @Nonnull
    private final CssMetaData<?, Dimension2D> cssMetaData;
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
    public PaperSizeStyleableMapAccessor(String name, MapAccessor<CssDimension> xKey, MapAccessor<CssDimension> yKey) {
        super(name, Dimension2D.class, new MapAccessor<?>[]{xKey, yKey}, new Dimension2D(xKey.getDefaultValue(), yKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<Dimension2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Dimension2D> cnvrtr
                = new StyleConverterAdapter<>(getConverter());
        CssMetaData<Styleable, Dimension2D> md
                = new SimpleCssMetaData<>(property, function,
                        cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.xKey = xKey;
        this.yKey = yKey;
    }
    @Nonnull
    @Override
    public Dimension2D get(Map<? super Key<?>, Object> a) {
      return new Dimension2D(xKey.get(a), yKey.get(a));
    }


    @Override
    public Converter<Dimension2D> getConverter() {
        if (converter == null) {
            converter = new CssPaperSizeConverter();
        }
        return converter;
    }
    @Nonnull
    @Override
    public CssMetaData<?, Dimension2D> getCssMetaData() {
      return cssMetaData;
      
    }
    @Override
    public boolean isNullable() {
      return false;
    }

    @Nonnull
    @Override
    public Dimension2D put(Map<? super Key<?>, Object> a, @Nonnull Dimension2D value) {
        Dimension2D oldValue = get(a);
        xKey.put(a, value.getX());
        yKey.put(a, value.getY());
        return oldValue;
    }

    @Nonnull
    @Override
    public Dimension2D remove(Map<? super Key<?>, Object> a) {
        Dimension2D oldValue = get(a);
        xKey.remove(a);
        yKey.remove(a);
        return oldValue;
    }

}
