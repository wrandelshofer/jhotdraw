/* @(#)DoubleStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssBooleanConverter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.CssConverterConverterAdapter;

/**
 * DoubleStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BooleanStyleableFigureKey extends AbstractStyleableFigureKey<Boolean> implements WriteableStyleableMapAccessor<Boolean> {

    final static long serialVersionUID = 1L;
    private final CssMetaData<? extends Styleable, Boolean> cssMetaData;

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
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public BooleanStyleableFigureKey(String name, Boolean defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
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
    public BooleanStyleableFigureKey(String key, DirtyMask mask, Boolean defaultValue) {
        super(key, Boolean.class, mask, defaultValue);

        StyleablePropertyFactory<? extends Styleable> factory = new StyleablePropertyFactory<>(null);
        cssMetaData = factory.createBooleanCssMetaData(
                Figure.JHOTDRAW_CSS_PREFIX + getCssName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<Boolean> converter;

    @Override
    public Converter<Boolean> getConverter() {
        if (converter == null) {
            converter = new CssConverterConverterAdapter<>(new CssBooleanConverter());
        }
        return converter;
    }
}
