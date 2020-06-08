/*
 * @(#)BooleanStyleableKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.StyleablePropertyFactory;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssBooleanConverter;

/**
 * Nullable BooleanStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class BooleanStyleableKey extends SimpleStyleableKey<Boolean>
        implements WriteableStyleableMapAccessor<Boolean>, NonNullMapAccessor<Boolean> {

    private final static long serialVersionUID = 1L;

    public BooleanStyleableKey(String key) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), false);
    }

    public BooleanStyleableKey(String key, Boolean defaultValue) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), defaultValue);
    }

    public BooleanStyleableKey(String key, String cssName) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), false);
    }

    public BooleanStyleableKey(String key, @NonNull String cssName, Boolean defaultValue) {
        super(key, Boolean.class, null, new CssBooleanConverter(false), defaultValue);
        setCssMetaData(
                new StyleablePropertyFactory<>(null).createBooleanCssMetaData(
                        cssName, s -> {
                            StyleablePropertyBean spb = (StyleablePropertyBean) s;
                            return spb.getStyleableProperty(this);
                        })
        );
    }

}
