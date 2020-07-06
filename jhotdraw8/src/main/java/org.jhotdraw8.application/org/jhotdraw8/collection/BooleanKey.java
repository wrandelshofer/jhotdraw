/*
 * @(#)BooleanKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

/**
 * NullableBooleanKey.
 *
 * @author Werner Randelshofer
 */
public class BooleanKey extends ObjectKey<Boolean> {

    private final static long serialVersionUID = 1L;

    public BooleanKey(@NonNull String key) {
        super(key, Boolean.class);
    }

    public BooleanKey(@NonNull String key, Boolean defaultValue) {
        super(key, Boolean.class, defaultValue);
    }
}
