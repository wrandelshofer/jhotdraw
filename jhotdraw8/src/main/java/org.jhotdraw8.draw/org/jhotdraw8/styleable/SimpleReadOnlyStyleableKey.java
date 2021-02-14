/*
 * @(#)SimpleReadOnlyStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.text.Converter;

import java.lang.reflect.Type;

/**
 * SimpleReadOnlyStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class SimpleReadOnlyStyleableKey<T> extends ObjectKey<T> implements ReadOnlyStyleableMapAccessor<T> {
    private final @NonNull String cssName;
    private static final long serialVersionUID = 1L;

    protected @Nullable CssMetaData<?, T> cssMetaData;
    protected @NonNull Converter<T> converter;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key       The name of the name.
     * @param clazz     The type of the value.
     * @param metaData  The CSS meta data.
     * @param converter the converter
     */
    public SimpleReadOnlyStyleableKey(@NonNull String key, @NonNull Type clazz, @NonNull CssMetaData<?, T> metaData, @NonNull Converter<T> converter) {
        this(key, clazz, metaData, converter, null);
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
    public SimpleReadOnlyStyleableKey(@NonNull String key, @NonNull Type clazz,
                                      @Nullable CssMetaData<?, T> metaData, @NonNull Converter<T> converter,
                                      @Nullable T defaultValue) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key), clazz, metaData, converter, defaultValue);

    }

    public SimpleReadOnlyStyleableKey(@NonNull String key, @NonNull String cssName, @NonNull Type clazz,
                                      @Nullable CssMetaData<?, T> metaData, @NonNull Converter<T> converter,
                                      @Nullable T defaultValue) {
        super(key, clazz, defaultValue == null, defaultValue);
        this.converter = converter;
        this.cssMetaData = metaData;
        this.cssName = cssName;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, T> getCssMetaData() {
        if (cssMetaData == null) {
            throw new IllegalStateException("cssMetadata has not been set yet.");
        }
        return cssMetaData;
    }

    /**
     * Setter method, if the css meta data can not be provided in the call to super() in the constructor.
     *
     * @param cssMetaData the meta data
     */
    protected void setCssMetaData(@NonNull CssMetaData<?, T> cssMetaData) {
        this.cssMetaData = cssMetaData;
    }

    @Override
    public @NonNull Converter<T> getCssConverter() {
        return converter;
    }

    /**
     * Setter method, if the converter can not be provided in the call to super() in the constructor.
     *
     * @param converter the converter
     */
    public void setConverter(@NonNull Converter<T> converter) {
        this.converter = converter;
    }

    public @NonNull String getCssName() {
        return cssName;
    }

}
