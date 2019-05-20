/* @(#)BooleanStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.text.CssBooleanConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * BooleanStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BooleanStyleableKey extends AbstractStyleableKey<Boolean>
        implements WriteableStyleableMapAccessor<Boolean>,
        NonnullMapAccessor<Boolean> {

    final static long serialVersionUID = 1L;
    private final CssMetaData<? extends Styleable, Boolean> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public BooleanStyleableKey(String name) {
        this(name, null);
    }


    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public BooleanStyleableKey(String key, Boolean defaultValue) {
        super(key, Boolean.class, defaultValue);

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
            converter = new CssBooleanConverter(false);
        }
        return converter;
    }
}
