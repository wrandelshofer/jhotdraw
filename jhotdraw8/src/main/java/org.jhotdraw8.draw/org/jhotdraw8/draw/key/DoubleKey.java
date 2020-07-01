/*
 * @(#)DoubleKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.NonNullKey;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 */
public class DoubleKey extends ObjectFigureKey<@NonNull Double> implements NonNullKey<@NonNull Double> {

    private final static long serialVersionUID = 1L;

    public DoubleKey(@NonNull String key) {
        this(key, 0.0);
    }

    public DoubleKey(@NonNull String key, @NonNull Double defaultValue) {
        super(key, Double.class, defaultValue);
    }
}
