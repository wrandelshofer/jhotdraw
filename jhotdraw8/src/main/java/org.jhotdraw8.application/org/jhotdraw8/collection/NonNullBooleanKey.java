/*
 * @(#)NonNullBooleanKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

/**
 * NonNullBooleanKey.
 *
 * @author Werner Randelshofer
 */
public class NonNullBooleanKey extends ObjectKey<@NonNull Boolean> implements NonNullMapAccessor<@NonNull Boolean> {

    private static final long serialVersionUID = 1L;

    public NonNullBooleanKey(@NonNull String key, @NonNull Boolean defaultValue) {
        super(key, Boolean.class, defaultValue);
    }
}
