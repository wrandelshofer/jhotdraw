/*
 * @(#)ReadOnlyStyleableMapAccessor.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * Interface for keys which support styled values from CSS.
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
     * Gets the CssMetaData.
     *
     * @return the meta data
     */
    @NonNull
    CssMetaData<@NonNull ? extends @NonNull Styleable, T> getCssMetaData();

    /**
     * Gets the converter.
     *
     * @return the converter
     */
    @NonNull
    Converter<T> getCssConverter();

    @NonNull
    default Converter<T> getXmlConverter() {
        return getCssConverter();
    }

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
    @Nullable
    default String getCssNamespace() {
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
    @NonNull
    static String toCssName(@NonNull String camelCaseName) {
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
