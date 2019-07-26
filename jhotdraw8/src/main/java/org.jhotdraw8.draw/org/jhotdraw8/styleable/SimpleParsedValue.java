/*
 * @(#)SimpleParsedValue.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;

/**
 * SimpleParsedValue.
 *
 * @param <V> type of the parsed value
 * @param <T> {@code StyleableProperty} type of the converted value
 * @author Werner Randelshofer
 */
public class SimpleParsedValue<V, T> extends ParsedValue<V, T> {

    public SimpleParsedValue(V value, StyleConverter<V, T> converter) {
        super(value, converter);
    }

}
