/* @(#)StringKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * StringKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StringKey extends ObjectKey<String> {

    private final static long serialVersionUID = 1L;

    public StringKey(String key) {
        super(key, String.class);
    }

    public StringKey(String key, String defaultValue) {
        super(key, String.class, defaultValue);
    }
}
