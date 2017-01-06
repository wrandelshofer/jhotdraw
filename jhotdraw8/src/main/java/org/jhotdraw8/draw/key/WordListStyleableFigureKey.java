/* @(#)WordListStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssWordListConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.styleable.StyleableMapAccessor;

/**
 * WordListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class WordListStyleableFigureKey extends SimpleFigureKey<ImmutableObservableList<String>> implements StyleableMapAccessor<ImmutableObservableList<String>> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, ImmutableObservableList<String>> cssMetaData;

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
    public WordListStyleableFigureKey(String name, ImmutableObservableList<String> defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public WordListStyleableFigureKey(String name, DirtyMask mask, ImmutableObservableList<String> defaultValue) {
        super(name, ImmutableObservableList.class, new Class<?>[]{String.class}, mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);

         Function<Styleable, StyleableProperty<ImmutableObservableList<String>>> function = s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         };
         boolean inherits = false;
         String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
         final StyleConverter<ParsedValue[], ImmutableObservableList<String>> converter
         = DoubleListStyleConverter.getInstance();
         CssMetaData<Styleable, ImmutableObservableList<String>> md
         = new SimpleCssMetaData<Styleable, ImmutableObservableList<String>>(property, function,
         converter, defaultValue, inherits);
         cssMetaData = md;*/
        Function<Styleable, StyleableProperty<ImmutableObservableList<String>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, ImmutableObservableList<String>> converter
                = new StyleConverterAdapter<ImmutableObservableList<String>>(new CssWordListConverter());
        CssMetaData<Styleable, ImmutableObservableList<String>> md
                = new SimpleCssMetaData<Styleable, ImmutableObservableList<String>>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?, ImmutableObservableList<String>> getCssMetaData() {
        return cssMetaData;
    }

    private Converter<ImmutableObservableList<String>> converter;

    @Override
    public Converter<ImmutableObservableList<String>> getConverter() {
        if (converter == null) {
            converter = new CssWordListConverter();
        }
        return converter;
    }

}
