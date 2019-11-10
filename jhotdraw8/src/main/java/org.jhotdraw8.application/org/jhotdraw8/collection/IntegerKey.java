/*
 * @(#)IntegerKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 */
public class IntegerKey extends ObjectKey<Integer> {

    private final static long serialVersionUID = 1L;

    public IntegerKey(@NonNull String key) {
        super(key, Integer.class);
    }

    public IntegerKey(@NonNull String key, Integer defaultValue) {
        super(key, Integer.class, defaultValue);
    }
}
