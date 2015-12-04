/* @(#)EnumStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.CssEnumConverter;
import org.jhotdraw.styleable.StyleableMapAccessor;

/**
 * EnumStyleableFigureKey.
 *
 * @author werni
 */
public class EnumStyleableFigureKey<T extends Enum<T>> extends SimpleFigureKey<T> implements StyleableMapAccessor<T> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?,T> cssMetaData;

    /**
     * Creates a new instance with the specified name, enum class, mask and with
     * null as the default value.
     *
     * @param name The name of the key.
     * @param clazz The enum class.
     * @param mask The mask.
     */
    public EnumStyleableFigureKey(String name, Class<T> clazz, DirtyMask mask) {
        this(name, clazz, mask, null);
    }

    /**
     * Creates a new instance with the specified name, enum class, mask and 
     * default value.
     *
     * @param name The name of the key.
     * @param clazz The enum class.
     * @param mask The mask.
     * @param defaultValue The default value.
     */
    public EnumStyleableFigureKey(String name, Class<T> clazz, DirtyMask mask, T defaultValue) {
        super(name, clazz, mask, defaultValue);

        StyleablePropertyFactory<?> factory = new StyleablePropertyFactory<Styleable>(null);
        cssMetaData = factory.createEnumCssMetaData(clazz,
                Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public CssMetaData<?,T> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<T> converter;

    @Override
    public Converter<T> getConverter() {
        if (converter == null) {
            converter = new CssEnumConverter<T>(getValueType());
        }
        return converter;
    }
}
