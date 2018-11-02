/* @(#)CssPoint2DStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javax.annotation.Nonnull;

import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssPoint2DConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * CssPoint2DStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssPoint2DStyleableFigureKey extends AbstractStyleableFigureKey<CssPoint2D> implements WriteableStyleableMapAccessor<CssPoint2D> {

    private final static long serialVersionUID = 1L;
    private final Converter<CssPoint2D> converter;

    @Nonnull
    private final CssMetaData<?, CssPoint2D> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public CssPoint2DStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public CssPoint2DStyleableFigureKey(String name, CssPoint2D defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name. type parameters are given. Otherwise
     * specify them in arrow brackets.
     * @param mask Dirty bit mask.
     * @param defaultValue The default value.
     */
    public CssPoint2DStyleableFigureKey(String key, DirtyMask mask, CssPoint2D defaultValue) {
        this(key,mask,defaultValue,new CssPoint2DConverter(false));
    }
    public CssPoint2DStyleableFigureKey(String key, DirtyMask mask, CssPoint2D defaultValue, CssConverter<CssPoint2D> converter) {
        super(key, CssPoint2D.class, mask, defaultValue);

        Function<Styleable, StyleableProperty<CssPoint2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        this.converter=converter;
        final StyleConverter<String, CssPoint2D> c
                = new StyleConverterAdapter<>(new CssPoint2DConverter(false));
        CssMetaData<Styleable, CssPoint2D> md
                = new SimpleCssMetaData<>(property, function,
                c, defaultValue, inherits);
        cssMetaData = md;
    }


    @Override
    public Converter<CssPoint2D> getConverter() {
       return converter;
    }
    @Nonnull
    @Override
    public CssMetaData<?, CssPoint2D> getCssMetaData() {
      return cssMetaData;
      
    }
}
