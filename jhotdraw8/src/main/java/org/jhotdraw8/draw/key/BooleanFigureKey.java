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

/**
 * DoubleStyleableFigureKey.
 *
 * @author werni
 */
public class BooleanFigureKey extends SimpleFigureKey<Boolean> {

    final static long serialVersionUID = 1L;
  

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public BooleanFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public BooleanFigureKey(String name, Boolean defaultValue) {
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
    public BooleanFigureKey(String key, DirtyMask mask, Boolean defaultValue) {
        super(key, Boolean.class, mask, defaultValue);

    }

    
}
