/* @(#)BlendModeStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.effect.BlendMode;
import org.jhotdraw.draw.css.StyleableKey;
import org.jhotdraw.draw.css.StyleablePropertyBean;
import org.jhotdraw.draw.DirtyBits;
import org.jhotdraw.draw.DirtyMask;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.SimpleFigureKey;

/**
 * BlendModeStyleableFigureKey.
 * @author Werner Randelshofer
 */
public class BlendModeStyleableFigureKey extends SimpleFigureKey<BlendMode> implements StyleableKey<BlendMode> {

    
    private final CssMetaData cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public BlendModeStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default
     * value.
     *
     * @param name The name of the key. 
     * @param defaultValue The default value.
     */
    public BlendModeStyleableFigureKey(String name, BlendMode defaultValue) {
        super(name, BlendMode.class, DirtyMask.of(DirtyBits.NODE), defaultValue);

        StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
        cssMetaData = factory.createEnumCssMetaData(BlendMode.class,
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
