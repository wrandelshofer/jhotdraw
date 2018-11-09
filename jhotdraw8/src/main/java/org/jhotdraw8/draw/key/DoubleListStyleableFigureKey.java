/* @(#)DoubleListStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javax.annotation.Nonnull;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssDoubleConverter;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * DoubleListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DoubleListStyleableFigureKey extends AbstractStyleableFigureKey<ImmutableList<Double>> implements WriteableStyleableMapAccessor<ImmutableList<Double>> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, ImmutableList<Double>> cssMetaData;
    private Converter<ImmutableList<Double>> converter;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public DoubleListStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public DoubleListStyleableFigureKey(String name, ImmutableList<Double> defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public DoubleListStyleableFigureKey(String name, DirtyMask mask, ImmutableList<Double> defaultValue) {
        super(name, ImmutableList.class, new Class<?>[]{Double.class}, mask, defaultValue);

        Function<Styleable, StyleableProperty<ImmutableList<Double>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        converter = new CssListConverter<>(new CssDoubleConverter(false));
        CssMetaData<Styleable, ImmutableList<Double>> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), defaultValue, inherits);
        cssMetaData = md;
    }

    @Nonnull
    @Override
    public CssMetaData<?, ImmutableList<Double>> getCssMetaData() {
        return cssMetaData;
    }

    @Override
    public Converter<ImmutableList<Double>> getConverter() {
        return converter;
    }

}
