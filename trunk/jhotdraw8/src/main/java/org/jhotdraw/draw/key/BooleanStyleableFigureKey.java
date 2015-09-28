/* @(#)DoubleStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleablePropertyFactory;
import org.jhotdraw.draw.css.StyleableKey;
import org.jhotdraw.draw.css.StyleablePropertyBean;
import org.jhotdraw.draw.Figure;

/**
 * DoubleStyleableFigureKey.
 * @author werni
 */
public class BooleanStyleableFigureKey extends SimpleFigureKey<Boolean> implements StyleableKey<Boolean> {

    
    private final CssMetaData cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public BooleanStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default
     * value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public BooleanStyleableFigureKey(String name, Boolean defaultValue) {
        this(name,DirtyMask.of(DirtyBits.NODE),defaultValue);
    }
    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name. type parameters are given. Otherwise
     * specify them in arrow brackets.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public BooleanStyleableFigureKey(String key,DirtyMask mask, Boolean defaultValue) {
        super(key, Boolean.class, mask, defaultValue);

        StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
        cssMetaData = factory.createBooleanCssMetaData(
                Figure.JHOTDRAW_CSS_PREFIX + getCssName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public CssMetaData getCssMetaData() {
        return cssMetaData;

    }

}
