/*
 * @(#)DoubleKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 */
public class DoubleKey extends ObjectKey<Double> {

    private final static long serialVersionUID = 1L;

    public DoubleKey(String key) {
        super(key, Double.class);
    }

    public DoubleKey(String key, Double defaultValue) {
        super(key, Double.class, defaultValue);
    }
}
