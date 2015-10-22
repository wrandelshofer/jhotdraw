/* @(#)WordListStyleableFigureKey.java
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
import org.jhotdraw.text.CssWordListConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;

/**
 * WordListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class WordListStyleableFigureKey extends SimpleFigureKey<List<String>> implements StyleableKey<List<String>> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, List<String>> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public WordListStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public WordListStyleableFigureKey(String name, List<String> defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public WordListStyleableFigureKey(String name, DirtyMask mask, List<String> defaultValue) {
        super(name, List.class, "<Double>", mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);

         Function<Styleable, StyleableProperty<List<String>>> function = s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         };
         boolean inherits = false;
         String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
         final StyleConverter<ParsedValue[], List<String>> converter
         = DoubleListStyleConverter.getInstance();
         CssMetaData<Styleable, List<String>> md
         = new SimpleCssMetaData<Styleable, List<String>>(property, function,
         converter, defaultValue, inherits);
         cssMetaData = md;*/
        Function<Styleable, StyleableProperty<List<String>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, List<String>> converter
                = new StyleConverterConverterWrapper<List<String>>(new CssWordListConverter());
        CssMetaData<Styleable, List<String>> md
                = new SimpleCssMetaData<Styleable, List<String>>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?, List<String>> getCssMetaData() {
        return cssMetaData;

    }
}
