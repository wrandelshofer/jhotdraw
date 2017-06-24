/* @(#)SimpleStyleableKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.text.Converter;

/**
 * SimpleStyleableKey.
 *
 * @author werni
 */
public class SimpleStyleableKey<T> extends ObjectKey<T> implements StyleableMapAccessor<T> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, T> cssMetaData;
    private final Converter<T> converter;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param metaData The CSS meta data.
     * @param converter the converter
     */
    public SimpleStyleableKey(String key, Class<T> clazz, CssMetaData<?, T> metaData, Converter<T> converter) {
        this(key, clazz, null, metaData, converter, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param metaData The CSS meta data.
     * @param converter the converter
     * @param defaultValue The default value.
     */
    public SimpleStyleableKey(String key, Class<T> clazz, CssMetaData<?, T> metaData, Converter<T> converter, T defaultValue) {
        this(key, clazz, null, metaData, converter, defaultValue);
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
     * @param converter the converter
     * @param defaultValue The default value.
     */
    public SimpleStyleableKey(String key, Class<?> clazz, Class<?>[] typeParameters, CssMetaData<?, T> metaData, Converter<T> converter, T defaultValue) {
        super(key, clazz, typeParameters, defaultValue);
        this.converter = converter;
        this.cssMetaData = metaData;
    }

    @Override
    public CssMetaData<?, T> getCssMetaData() {
        return null;
    }

    @Override
    public Converter<T> getConverter() {
        return converter;
    }

}
