/* @(#)DoubleStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javax.annotation.Nonnull;

import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssDoubleConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * DoubleStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DoubleStyleableFigureKey extends AbstractStyleableFigureKey<Double> implements WriteableStyleableMapAccessor<Double> {
    final static long serialVersionUID = 1L;
    @Nonnull
    private final CssMetaData<? extends Styleable, Double> cssMetaData;

    private final Converter<Double> converter ;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public DoubleStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public DoubleStyleableFigureKey(String name, Double defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public DoubleStyleableFigureKey(String name, DirtyMask mask, Double defaultValue) {
        this(name,mask,defaultValue,new CssDoubleConverter(false));
    }
    public DoubleStyleableFigureKey(String name, Double defaultValue, CssConverter<Double> converter) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue,converter);
    }
    public DoubleStyleableFigureKey(String name, DirtyMask mask, Double defaultValue, CssConverter<Double> converter) {
        super(name, Double.class, mask, defaultValue);

        Function<Styleable, StyleableProperty<Double>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        this.converter = converter;
        CssMetaData<Styleable, Double> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), defaultValue, inherits);
        cssMetaData = md;
    }

    @Nonnull
    @Override
    public CssMetaData<? extends Styleable, Double> getCssMetaData() {
        return cssMetaData;

    }

    @Nonnull
    @Override
    public Converter<Double> getConverter() {
        return converter;
    }
}
