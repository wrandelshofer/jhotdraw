/* @(#)StyleableKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.css;

import javafx.css.CssMetaData;
import org.jhotdraw.collection.Key;

/**
 * Interface for keys which support styled values from CSS.
 *
 * @author werni
 * @param <T> The value type.
 */
public interface StyleableKey<T> extends Key<T> {

    /**
     * Gets the CssMetaData.
     * @return the meta data
     */
    CssMetaData getCssMetaData();

    /**
     * Returns the CSS name string.
     * <p>
     * The default implementation converts the name from "camel case" to 
     * "dash separated words".
     *
     * @return name string.
     */
    default String getCssName() {
        StringBuilder b = new StringBuilder();
        String name = getName();
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
