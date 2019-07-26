/*
 * @(#)SimpleReadOnlyStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.text.Converter;

/**
 * SimpleStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleReadOnlyStyleableKey<T> extends ObjectKey<T> implements ReadOnlyStyleableMapAccessor<T> {
    @Nonnull
    private final String cssName;
    private final static long serialVersionUID = 1L;

    private CssMetaData<?, T> cssMetaData;
    private Converter<T> converter;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key       The name of the name.
     * @param clazz     The type of the value.
     * @param metaData  The CSS meta data.
     * @param converter the converter
     */
    public SimpleReadOnlyStyleableKey(String key, Class<T> clazz, CssMetaData<?, T> metaData, Converter<T> converter) {
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
    public SimpleReadOnlyStyleableKey(String key, Class<T> clazz, CssMetaData<?, T> metaData, Converter<T> converter, T defaultValue) {
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
    public SimpleReadOnlyStyleableKey(String key, Class<?> clazz, Class<?>[] typeParameters, CssMetaData<?, T> metaData, Converter<T> converter, T defaultValue) {
        super(key, clazz, typeParameters, defaultValue);
        this.converter = converter;
        this.cssMetaData = metaData;
        this.cssName = ReadOnlyStyleableMapAccessor.toCssName(key);
    }

    @Override
    public CssMetaData<?, T> getCssMetaData() {
        return cssMetaData;
    }

    /**
     * Setter method, if the css meta data can not be provided in the call to super() in the constructor.
     *
     * @param cssMetaData the meta data
     */
    protected void setCssMetaData(CssMetaData<?, T> cssMetaData) {
        this.cssMetaData = cssMetaData;
    }

    @Override
    public Converter<T> getConverter() {
        return converter;
    }

    /**
     * Setter method, if the converter can not be provided in the call to super() in the constructor.
     *
     * @param converter the converter
     */
    public void setConverter(Converter<T> converter) {
        this.converter = converter;
    }

    @Nonnull
    public String getCssName() {
        return cssName;
    }

}
