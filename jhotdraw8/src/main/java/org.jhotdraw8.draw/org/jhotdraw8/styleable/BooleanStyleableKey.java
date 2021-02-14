/*
 * @(#)BooleanStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.text.CssBooleanConverter;

/**
 * Nullable BooleanStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class BooleanStyleableKey extends SimpleStyleableKey<@Nullable Boolean>
        implements WriteableStyleableMapAccessor<@Nullable Boolean> {

    private static final long serialVersionUID = 1L;

    public BooleanStyleableKey(@NonNull String key) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), false);
    }

    public BooleanStyleableKey(@NonNull String key, Boolean defaultValue) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), defaultValue);
    }

    public BooleanStyleableKey(@NonNull String key, String cssName) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), false);
    }

    public BooleanStyleableKey(@NonNull String key, @NonNull String cssName, @Nullable Boolean defaultValue) {
        super(key, Boolean.class, new CssBooleanConverter(false), defaultValue);

    }

}
