/* @(#)CssDimensionStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javax.annotation.Nonnull;

import org.jhotdraw8.css.text.CssDimension;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * CssDimensionStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssDimensionStyleableFigureKey extends AbstractStyleableFigureKey<CssDimension> implements WriteableStyleableMapAccessor<CssDimension> {

    final static long serialVersionUID = 1L;

    private final Converter<CssDimension> converter = new CssSizeConverter(false);
    @Nonnull
    private final CssMetaData<? extends Styleable, CssDimension> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public CssDimensionStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public CssDimensionStyleableFigureKey(String name, CssDimension defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public CssDimensionStyleableFigureKey(String name, DirtyMask mask, CssDimension defaultValue) {
        super(name, CssDimension.class, mask, defaultValue);

        Function<Styleable, StyleableProperty<CssDimension>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, CssDimension> cvrtr
                = new StyleConverterAdapter<>(converter);
        CssMetaData<Styleable, CssDimension> md
                = new SimpleCssMetaData<>(property, function,
                cvrtr, defaultValue, inherits);
        cssMetaData = md;
    }


    @Nonnull
    @Override
    public Converter<CssDimension> getConverter() {
        return converter;
    }
    @Nonnull
    @Override
    public CssMetaData<? extends Styleable, CssDimension> getCssMetaData() {
      return cssMetaData;
      
    }
}
