/* @(#)FontStyleableMapAccessor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.key;

import java.util.Map;
import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssFFontConverter;
import org.jhotdraw8.text.StyleConverterConverterWrapper;
import org.jhotdraw8.text.FFont;

/**
 * FontStyleableMapAccessor.
 *
 * @author werni
 */
public class FontStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<FFont> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, FFont> cssMetaData;
    private final MapAccessor<String> familyKey;
    private final MapAccessor<FontWeight> weightKey;
    private final MapAccessor<FontPosture> postureKey;
    private final MapAccessor<Double> sizeKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param familyKey the font family key
     * @param weightKey the font weight key
     * @param postureKey the font posture key
     * @param sizeKey the font size key
     */
    public FontStyleableMapAccessor(String name, MapAccessor<String> familyKey, MapAccessor<FontWeight> weightKey, MapAccessor<FontPosture> postureKey, MapAccessor<Double> sizeKey) {
        super(name, FFont.class, new MapAccessor<?>[]{familyKey, sizeKey, weightKey, postureKey}, FFont.font(familyKey.getDefaultValue(), weightKey.getDefaultValue(), postureKey.getDefaultValue(), sizeKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<FFont>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, FFont> converter
                = new StyleConverterConverterWrapper<FFont>(new CssFFontConverter());
        CssMetaData<Styleable, FFont> md
                = new SimpleCssMetaData<Styleable, FFont>(property, function,
                        converter, getDefaultValue(), inherits);
        cssMetaData = md;

        this.familyKey = familyKey;
        this.sizeKey = sizeKey;
        this.weightKey = weightKey;
        this.postureKey = postureKey;
    }

    @Override
    public CssMetaData<?, FFont> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<FFont> converter;

    @Override
    public Converter<FFont> getConverter() {
        if (converter == null) {
            converter = new CssFFontConverter();
        }
        return converter;
    }

    @Override
    public FFont get(Map<? super Key<?>, Object> a) {
        FFont f =  FFont.font(familyKey.get(a), weightKey.get(a), postureKey.get(a), sizeKey.get(a));
        return f;
    }

    @Override
    public FFont put(Map<? super Key<?>, Object> a, FFont value) {
        FFont oldValue = get(a);
        familyKey.put(a, value.getFamily());
        weightKey.put(a, value.getWeight()); 
        postureKey.put(a, value.getPosture());
        sizeKey.put(a, value.getSize());
        return oldValue;
    }

    @Override
    public FFont remove(Map<? super Key<?>, Object> a) {
        FFont oldValue = get(a);
        familyKey.remove(a);
        weightKey.remove(a);
        postureKey.remove(a);
        sizeKey.remove(a);
        return oldValue;
    }

    @Override
    public boolean isNullable() {
        return false;
    }
}
