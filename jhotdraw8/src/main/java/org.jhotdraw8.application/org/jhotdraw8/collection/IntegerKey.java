/*
 * @(#)IntegerKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntegerKey extends ObjectKey<Integer> {

    private final static long serialVersionUID = 1L;

    public IntegerKey(String key) {
        super(key, Integer.class);
    }

    public IntegerKey(String key, Integer defaultValue) {
        super(key, Integer.class, defaultValue);
    }
}
