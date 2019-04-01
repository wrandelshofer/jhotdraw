/* @(#)NullableBooleanKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * NullableBooleanKey.
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
