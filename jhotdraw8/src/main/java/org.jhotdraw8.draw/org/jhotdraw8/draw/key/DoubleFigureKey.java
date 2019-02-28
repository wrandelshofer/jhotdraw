/* @(#)DoubleFigureKey.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

/**
 * DoubleFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DoubleFigureKey extends ObjectFigureKey<Double> {

    private final static long serialVersionUID = 1L;

    public DoubleFigureKey(String key, DirtyMask dirtyMask) {
        this(key, dirtyMask, 0.0);
    }

    public DoubleFigureKey(String key, DirtyMask dirtyMask, Double defaultValue) {
        super(key, Double.class, dirtyMask, defaultValue);
    }
}
