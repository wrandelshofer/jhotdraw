/*
 * @(#)NulllableBooleanStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.CssBooleanConverter;

/**
 * Nullable BooleanStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class NulllableBooleanStyleableKey extends SimpleStyleableKey<Boolean> implements WriteableStyleableMapAccessor<Boolean> {

    private static final long serialVersionUID = 1L;

    public NulllableBooleanStyleableKey(String key) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), null);
    }

    public NulllableBooleanStyleableKey(String key, Boolean defaultValue) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), defaultValue);
    }

    public NulllableBooleanStyleableKey(String key, String cssName) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), null);
    }

    public NulllableBooleanStyleableKey(String key, @NonNull String cssName, Boolean defaultValue) {
        super(key, Boolean.class, new CssBooleanConverter(true), defaultValue);
    }

}
