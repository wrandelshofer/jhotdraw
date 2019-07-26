/*
 * @(#)NonnullBooleanKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * NonnullBooleanKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NonnullBooleanKey extends ObjectKey<Boolean> implements NonnullMapAccessor<Boolean> {

    private final static long serialVersionUID = 1L;

    public NonnullBooleanKey(String key) {
        super(key, Boolean.class);
    }

    public NonnullBooleanKey(String key, Boolean defaultValue) {
        super(key, Boolean.class, defaultValue);
    }
}
