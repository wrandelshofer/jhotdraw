/*
 * @(#)NonNullBooleanKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

/**
 * NonNullBooleanKey.
 *
 * @author Werner Randelshofer
 */
public class NonNullBooleanKey extends ObjectKey<Boolean> implements NonNullMapAccessor<Boolean> {

    private final static long serialVersionUID = 1L;

    public NonNullBooleanKey(@NonNull String key, Boolean defaultValue) {
        super(key, Boolean.class, defaultValue);
    }
}
