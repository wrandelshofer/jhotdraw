/* @(#)Size2DStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javax.annotation.Nonnull;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssSize2D;
import org.jhotdraw8.text.CssSize2DConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * Size2DStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Size2DStyleableFigureKey extends AbstractStyleableFigureKey<CssSize2D> implements WriteableStyleableMapAccessor<CssSize2D> {

    private final static long serialVersionUID = 1L;
    private Converter<CssSize2D> converter;

    @Nonnull
    private final CssMetaData<?, CssSize2D> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public Size2DStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public Size2DStyleableFigureKey(String name, CssSize2D defaultValue) {
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
    public Size2DStyleableFigureKey(String key, DirtyMask mask, CssSize2D defaultValue) {
        super(key, CssSize2D.class, mask, defaultValue);

        Function<Styleable, StyleableProperty<CssSize2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, CssSize2D> converter
                = new StyleConverterAdapter<CssSize2D>(new CssSize2DConverter());
        CssMetaData<Styleable, CssSize2D> md
                = new SimpleCssMetaData<Styleable, CssSize2D>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }


    @Override
    public Converter<CssSize2D> getConverter() {
        if (converter == null) {
            converter = new CssSize2DConverter();
        }
        return converter;
    }
    @Nonnull
    @Override
    public CssMetaData<?, CssSize2D> getCssMetaData() {
      return cssMetaData;
      
    }
}
