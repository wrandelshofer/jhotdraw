/* @(#)FontStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.SimpleKey;
import org.jhotdraw.draw.css.StyleableKey;
import org.jhotdraw.draw.css.StyleablePropertyBean;
import org.jhotdraw.draw.DirtyBits;
import org.jhotdraw.draw.DirtyMask;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.SimpleFigureKey;

/**
 * FontStyleableFigureKey.
 * @author werni
 */
public class FontStyleableFigureKey extends SimpleFigureKey<Font> implements StyleableKey<Font> {

    
    private final CssMetaData cssMetaData;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key The name of the name.
     * @param metaData The CSS meta data.
     */
    public FontStyleableFigureKey(String key) {
        this(key, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name. type parameters are given. Otherwise
     * specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public FontStyleableFigureKey(String key, Font defaultValue) {
        super(key, Font.class, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), defaultValue);

        StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
        cssMetaData = factory.createFontCssMetaData(
                Figure.JHOTDRAW_CSS_PREFIX + getCssName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public CssMetaData createCssMetaData() {
        return cssMetaData;

    }

}
