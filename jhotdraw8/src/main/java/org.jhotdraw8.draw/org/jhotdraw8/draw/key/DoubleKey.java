/*
 * @(#)DoubleKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.model.DirtyMask;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 */
public class DoubleKey extends ObjectFigureKey<Double> {

    private final static long serialVersionUID = 1L;

    public DoubleKey(@NonNull String key, DirtyMask dirtyMask) {
        this(key, dirtyMask, 0.0);
    }

    public DoubleKey(@NonNull String key, DirtyMask dirtyMask, @NonNull Double defaultValue) {
        super(key, Double.class, defaultValue);
    }
}
