/*
 * @(#)SimpleStyleableKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import org.jhotdraw8.text.Converter;

/**
 * SimpleStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class SimpleStyleableKey<T> extends SimpleReadOnlyStyleableKey<T> {

    private final static long serialVersionUID = 1L;


    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key       The name of the name.
     * @param clazz     The type of the value.
     * @param metaData  The CSS meta data.
     * @param converter the converter
     */
    public SimpleStyleableKey(String key, Class<T> clazz, CssMetaData<?, T> metaData, Converter<T> converter) {
        this(key, clazz, null, metaData, converter, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key          The name of the name.
     * @param clazz        The type of the value.
     * @param metaData     The CSS meta data.
     * @param converter    the converter
     * @param defaultValue The default value.
     */
    public SimpleStyleableKey(String key, Class<T> clazz, CssMetaData<?, T> metaData, Converter<T> converter, T defaultValue) {
        this(key, clazz, null, metaData, converter, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key            The name of the name.
     * @param clazz          The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     *                       type parameters are given. Otherwise specify them in arrow brackets.
     * @param metaData       The CSS meta data.
     * @param converter      the converter
     * @param defaultValue   The default value.
     */
    public SimpleStyleableKey(String key, Class<?> clazz, Class<?>[] typeParameters, CssMetaData<?, T> metaData, Converter<T> converter, T defaultValue) {
        super(key, clazz, typeParameters, metaData, converter, defaultValue);
    }


}
