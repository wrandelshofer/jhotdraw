/* @(#)DoubleKey.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DoubleKey extends ObjectFigureKey<Double> {

    private final static long serialVersionUID = 1L;

    public DoubleKey(String key, DirtyMask dirtyMask) {
        this(key, dirtyMask, 0.0);
    }

    public DoubleKey(String key, DirtyMask dirtyMask, Double defaultValue) {
        super(key, Double.class, defaultValue);
    }
}
