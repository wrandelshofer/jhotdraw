/* @(#)BooleanKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.collection;

/**
 * BooleanKey.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BooleanKey extends Key<Boolean> {

    public BooleanKey(String key) {
        super(key, Boolean.class);
    }

    public BooleanKey(String key, Boolean defaultValue) {
        super(key, Boolean.class, defaultValue);
    }
}
