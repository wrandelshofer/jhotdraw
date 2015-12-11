/* @(#)TransformListStyleableFigureKey.java
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
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.CssSizeListConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;
import org.jhotdraw.styleable.StyleableMapAccessor;
import javafx.scene.transform.Transform;
import org.jhotdraw.text.CssTransformListConverter;

/**
 * TransformListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class TransformListStyleableFigureKey extends SimpleFigureKey<List<Transform>> implements StyleableMapAccessor<List<Transform>> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, List<Transform>> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public TransformListStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public TransformListStyleableFigureKey(String name, List<Transform> defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public TransformListStyleableFigureKey(String name, DirtyMask mask, List<Transform> defaultValue) {
        super(name, List.class, new Class<?>[]{Transform.class}, mask, defaultValue);

        Function<Styleable, StyleableProperty<List<Transform>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, List<Transform>> converter
                = new StyleConverterConverterWrapper<List<Transform>>(new CssTransformListConverter());
        CssMetaData<Styleable, List<Transform>> md
                = new SimpleCssMetaData<Styleable, List<Transform>>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?, List<Transform>> getCssMetaData() {
        return cssMetaData;
    }


    private Converter<List<Transform>> converter;

    @Override
    public Converter<List<Transform>> getConverter() {
        if (converter == null) {
            converter = new CssTransformListConverter();
        }
        return converter;
    }
    
    
}
