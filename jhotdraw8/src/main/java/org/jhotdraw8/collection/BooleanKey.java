/* @(#)BooleanKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

/**
 * BooleanKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BooleanKey extends SimpleKey<Boolean> {

    private final static long serialVersionUID = 1L;

    public BooleanKey(String key) {
        super(key, Boolean.class);
    }

    public BooleanKey(String key, Boolean defaultValue) {
        super(key, Boolean.class, defaultValue);
    }
}
