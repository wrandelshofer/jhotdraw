/* @(#)InsetsStyleableMapAccessor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import java.util.Map;
import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.CssInsetsConverter;
import org.jhotdraw.text.StyleConverterConverterWrapper;

/**
 * InsetsStyleableMapAccessor.
 *
 * @author werni
 */
public class InsetsStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<Insets> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, Insets> cssMetaData;
    private final MapAccessor<Double> topKey;
    private final MapAccessor<Double> rightKey;
    private final MapAccessor<Double> bottomKey;
    private final MapAccessor<Double> leftKey;

    /**
     * Creates a new instance with the specified name.
     * 
     * @param name the name of the accessor
     * @param topKey the insets top key
     * @param rightKey the insets right key
     * @param bottomKey the insets bottom key
     * @param leftKey the insets left key
     */
    public InsetsStyleableMapAccessor(String name, MapAccessor<Double> topKey, MapAccessor<Double> rightKey, MapAccessor<Double> bottomKey, MapAccessor<Double> leftKey) {
        super(name, Insets.class, new MapAccessor<?>[]{topKey, rightKey, bottomKey, leftKey}, new Insets(topKey.getDefaultValue(), rightKey.getDefaultValue(), bottomKey.getDefaultValue(), leftKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<Insets>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Insets> cnvrtr
                = new StyleConverterConverterWrapper<>(getConverter());
        CssMetaData<Styleable, Insets> md
                = new SimpleCssMetaData<>(property, function,
                        cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.topKey = topKey;
        this.rightKey = rightKey;
        this.bottomKey = bottomKey;
        this.leftKey = leftKey;
    }

    @Override
    public CssMetaData<?, Insets> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<Insets> converter;

    @Override
    public Converter<Insets> getConverter() {
        if (converter == null) {
            converter = new CssInsetsConverter();
        }
        return converter;
    }

    @Override
    public Insets get(Map<? super Key<?>, Object> a) {
        return new Insets(topKey.get(a), rightKey.get(a), bottomKey.get(a), leftKey.get(a));
    }

    @Override
    public Insets put(Map<? super Key<?>, Object> a, Insets value) {
        Insets oldValue = get(a);
        topKey.put(a, value.getTop());
        rightKey.put(a, value.getRight());
        bottomKey.put(a, value.getBottom());
        leftKey.put(a, value.getLeft());
        return oldValue;
    }

    @Override
    public Insets remove(Map<? super Key<?>, Object> a) {
        Insets oldValue = get(a);
        topKey.remove(a);
        rightKey.remove(a);
        bottomKey.remove(a);
        leftKey.remove(a);
        return oldValue;
    }

    @Override
    public boolean isNullable() {
        return false;
    }
}
