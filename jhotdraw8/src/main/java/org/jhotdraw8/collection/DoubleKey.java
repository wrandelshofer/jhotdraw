/* @(#)DoubleKey.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
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
