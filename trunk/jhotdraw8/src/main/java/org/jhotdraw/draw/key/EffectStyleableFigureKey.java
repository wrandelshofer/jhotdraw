/* @(#)EffectStyleableFigureKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Paint;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.SimpleKey;
import org.jhotdraw.styleable.StyleableKey;
import org.jhotdraw.styleable.StyleablePropertyBean;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.text.Converter;
import org.jhotdraw.text.CssEffectConverter;
import org.jhotdraw.text.EnumConverter;

/**
 * EffectStyleableFigureKey.
 * @author Werner Randelshofer
 */
public class EffectStyleableFigureKey extends SimpleFigureKey<Effect> implements StyleableKey<Effect> {

    final static long serialVersionUID = 1L;
    private final CssEffectConverter converter=new CssEffectConverter();
    private final CssMetaData<? extends Styleable, Effect> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public EffectStyleableFigureKey(String name) {
        this(name, null);
    }

   /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public EffectStyleableFigureKey(String name, Effect defaultValue) {
        super(name, Effect.class, DirtyMask.of(DirtyBits.NODE), defaultValue);

        StyleablePropertyFactory<? extends Styleable> factory = new StyleablePropertyFactory<>(null);
        cssMetaData = factory.createEffectCssMetaData(
                Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public CssMetaData<? extends Styleable, Effect> getCssMetaData() {
        return cssMetaData;

    }
   @Override
    public Converter<Effect> getConverter() {
       return converter;
    }
}
