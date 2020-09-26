/*
 * @(#)StringKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

/**
 * A nullable StringKey.
 *
 * @author Werner Randelshofer
 */
public class StringKey extends ObjectKey<String> {

    private final static long serialVersionUID = 1L;

    public StringKey(@NonNull String key) {
        super(key, String.class);
    }

    public StringKey(@NonNull String key, String defaultValue) {
        super(key, String.class, defaultValue);
    }
}
