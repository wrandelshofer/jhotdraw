/* @(#)SimpleStyleableKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import javafx.css.CssMetaData;
import org.jhotdraw.collection.Key;
import org.jhotdraw.collection.SimpleKey;

/**
 * SimpleStyleableKey.
 *
 * @author werni
 */
public class SimpleStyleableKey<T> extends SimpleKey<T> implements StyleableKey<T> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, T> cssMetaData;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param metaData The CSS meta data.
     */
    public SimpleStyleableKey(String key, Class<T> clazz, CssMetaData<?, T> metaData) {
        this(key, clazz, "", metaData, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param metaData The CSS meta data.
     * @param defaultValue The default value.
     */
    public SimpleStyleableKey(String key, Class<T> clazz, CssMetaData<?, T> metaData, T defaultValue) {
        this(key, clazz, "", metaData, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     * type parameters are given. Otherwise specify them in arrow brackets.
     * @param metaData The CSS meta data.
     * @param defaultValue The default value.
     */
    public SimpleStyleableKey(String key, Class<?> clazz, String typeParameters, CssMetaData<?, T> metaData, T defaultValue) {
        super(key, clazz, typeParameters, defaultValue);
        this.cssMetaData = metaData;
    }

    @Override
    public CssMetaData<?, T> getCssMetaData() {
        return null;
    }

}
