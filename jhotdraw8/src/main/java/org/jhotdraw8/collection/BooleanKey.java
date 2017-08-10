/* @(#)BooleanKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * BooleanKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BooleanKey extends ObjectKey<Boolean> {

    private final static long serialVersionUID = 1L;

    public BooleanKey(String key) {
        super(key, Boolean.class);
    }

    public BooleanKey(String key, Boolean defaultValue) {
        super(key, Boolean.class, defaultValue);
    }
}
