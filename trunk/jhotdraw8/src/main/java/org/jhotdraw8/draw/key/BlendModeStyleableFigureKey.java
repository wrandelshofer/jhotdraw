/* @(#)BlendModeStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.effect.BlendMode;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssEnumConverter;
import org.jhotdraw8.styleable.StyleableMapAccessor;

/**
 * BlendModeStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class BlendModeStyleableFigureKey extends SimpleFigureKey<BlendMode> implements StyleableMapAccessor<BlendMode> {

    final static long serialVersionUID = 1L;

    private final CssMetaData<? extends Styleable, BlendMode> cssMetaData;

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
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public BlendModeStyleableFigureKey(String name, BlendMode defaultValue) {
        super(name, BlendMode.class, DirtyMask.of(DirtyBits.NODE), defaultValue);

        StyleablePropertyFactory<? extends Styleable> factory = new StyleablePropertyFactory<>(null);
        cssMetaData = factory.createEnumCssMetaData(BlendMode.class,
                Figure.JHOTDRAW_CSS_PREFIX + getCssName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public CssMetaData<? extends Styleable, BlendMode> getCssMetaData() {
        return cssMetaData;

    }
    private Converter<BlendMode> converter;

    @Override
    public Converter<BlendMode> getConverter() {
        if (converter == null) {
            converter = new CssEnumConverter<BlendMode>(BlendMode.class);
        }
        return converter;
    }

}
