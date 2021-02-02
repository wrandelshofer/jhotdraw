/*
 * @(#)SimpleStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.text.Converter;

import java.lang.reflect.Type;

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
     * @param type      The type of the value.
     * @param metaData  The CSS meta data.
     * @param converter the converter
     */
    public SimpleStyleableKey(@NonNull String key, @NonNull Type type, @Nullable CssMetaData<?, T> metaData, @NonNull Converter<T> converter) {
        this(key, type, metaData, converter, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key          The name of the name.
     * @param type         The type of the value.
     * @param metaData     The CSS meta data.
     * @param converter    the converter
     * @param defaultValue The default value.
     */
    public SimpleStyleableKey(@NonNull String key, @NonNull Type type, @Nullable CssMetaData<?, T> metaData, @NonNull Converter<T> converter, T defaultValue) {
        super(key, type, metaData, converter, defaultValue);
    }

    public SimpleStyleableKey(@NonNull String key, @NonNull String cssName, @NonNull Type type, @Nullable CssMetaData<?, T> metaData, @NonNull Converter<T> converter, T defaultValue) {
        super(key, cssName, type, metaData, converter, defaultValue);
    }


}
