/*
 * @(#)ReadOnlyStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * Generic interface for map accessors that are readable by CSS.
 * <p>
 * This interface does not guarantee 'read-only', it actually guarantees
 * 'readable'. We use the prefix 'ReadOnly' because this is the naming
 * convention in JavaFX for APIs that provide read methods but no write methods.
 *
 * @param <T> The value type.
 * @author Werner Randelshofer
 */
public interface ReadOnlyStyleableMapAccessor<T> extends MapAccessor<T> {

    long serialVersionUID = 1L;

    /**
     * Returns the name string.
     *
     * @return name string.
     */
    @NonNull String getName();

    /**
     * Gets the converter.
     *
     * @return the converter
     */
    @NonNull
    Converter<T> getCssConverter();

    /**
     * Returns the CSS name string.
     * <p>
     * The default implementation converts the name from "camel case" to "dash
     * separated words".
     *
     * @return name string.
     */
    @NonNull
    String getCssName();

    /**
     * Returns the CSS namespace uri.
     * <p>
     * The default implementation returns null.
     *
     * @return namespace uri string.
     */
    default @Nullable String getCssNamespace() {
        return null;
    }

    /**
     * Returns the CSS name string.
     * <p>
     * The default implementation converts the name from "camel case" to "dash
     * separated words".
     *
     * @param camelCaseName string
     * @return cssName string.
     */
    static @NonNull String toCssName(@NonNull String camelCaseName) {
        final StringBuilder b = new StringBuilder();
        final String name = camelCaseName;
        boolean insertDash = false;
        for (int i = 0, n = name.length(); i < n; i++) {
            char ch = name.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (insertDash) {
                    b.append('-');
                }
                b.append(Character.toLowerCase(ch));
                insertDash = false;
            } else {
                b.append(ch);
                insertDash = true;
            }
        }
        return b.toString();
    }

}
