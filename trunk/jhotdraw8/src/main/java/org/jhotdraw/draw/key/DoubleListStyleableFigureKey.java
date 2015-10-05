/* @(#)DoubleListStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import java.util.List;
import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.css.StyleableKey;
import org.jhotdraw.draw.css.StyleablePropertyBean;
import org.jhotdraw.text.CSSSizeListConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;

/**
 * DoubleListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class DoubleListStyleableFigureKey extends SimpleFigureKey<List<Double>> implements StyleableKey<List<Double>> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, List<Double>> cssMetaData;

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
    public DoubleListStyleableFigureKey(String name, List<Double> defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public DoubleListStyleableFigureKey(String name, DirtyMask mask, List<Double> defaultValue) {
        super(name, List.class, "<Double>", mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);

         Function<Styleable, StyleableProperty<List<Double>>> function = s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         };
         boolean inherits = false;
         String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
         final StyleConverter<ParsedValue[], List<Double>> converter
         = DoubleListStyleConverter.getInstance();
         CssMetaData<Styleable, List<Double>> md
         = new SimpleCssMetaData<Styleable, List<Double>>(property, function,
         converter, defaultValue, inherits);
         cssMetaData = md;*/
        Function<Styleable, StyleableProperty<List<Double>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, List<Double>> converter
                = new StyleConverterConverterWrapper<List<Double>>(new CSSSizeListConverter());
        CssMetaData<Styleable, List<Double>> md
                = new SimpleCssMetaData<Styleable, List<Double>>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?, List<Double>> getCssMetaData() {
        return cssMetaData;

    }
}
