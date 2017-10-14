/* @(#)DoubleFigureKey.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

/**
 * DoubleFigureKey.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DoubleFigureKey extends SimpleFigureKey<Double> {

    private final static long serialVersionUID = 1L;

    public DoubleFigureKey(String key, DirtyMask dirtyMask) {
        super(key, Double.class, dirtyMask);
    }

    public DoubleFigureKey(String key, DirtyMask dirtyMask, Double defaultValue) {
        super(key, Double.class, dirtyMask, defaultValue);
    }

    public DoubleFigureKey(String name, boolean isNullable, DirtyMask dirtyMask, Double defaultValue) {
        super(name, Double.class, isNullable, dirtyMask, defaultValue);
    }

}
