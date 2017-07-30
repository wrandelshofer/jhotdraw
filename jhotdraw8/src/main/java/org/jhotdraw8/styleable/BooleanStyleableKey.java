/* @(#)BooleanStyleableKey.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.styleable;

import javafx.css.StyleablePropertyFactory;
import org.jhotdraw8.text.CssBooleanConverter;

/**
 * BooleanStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class BooleanStyleableKey extends SimpleStyleableKey<Boolean> {

    public BooleanStyleableKey(String key) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key));
    }

    public BooleanStyleableKey(String key, String cssName) {
        super(key, Boolean.class, null, new CssBooleanConverter());
        setCssMetaData(
                new StyleablePropertyFactory<>(null).createBooleanCssMetaData(
                        cssName, s -> {
                            StyleablePropertyBean spb = (StyleablePropertyBean) s;
                            return spb.getStyleableProperty(this);
                        })
        );
    }

}
