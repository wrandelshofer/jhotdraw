/* @(#)StringKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.collection;

/**
 * StringKey.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StringKey extends Key<String> {

    public StringKey(String key) {
        super(key, String.class);
    }

    public StringKey(String key, String defaultValue) {
        super(key, String.class, defaultValue);
    }
}
