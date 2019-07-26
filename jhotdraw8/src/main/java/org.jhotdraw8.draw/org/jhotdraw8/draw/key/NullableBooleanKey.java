/*
 * @(#)NullableBooleanKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

/**
 * NullableBooleanKey.
 *
 * @author Werner Randelshofer
 */
public class NullableBooleanKey extends NullableObjectKey<Boolean> {

    final static long serialVersionUID = 1L;


    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public NullableBooleanKey(String name) {
        this(name, null);
    }


    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public NullableBooleanKey(String key, Boolean defaultValue) {
        super(key, Boolean.class, defaultValue);
    }


}
