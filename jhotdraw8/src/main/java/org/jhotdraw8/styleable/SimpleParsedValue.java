/* @(#)SimpleParsedValue.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.styleable;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;

/**
 * SimpleParsedValue.
 * @author Werner Randelshofer
 * @param <V> type of the parsed value
 * @param <T> {@code StyleableProperty} type of the converted value
 */
public class SimpleParsedValue<V,T> extends ParsedValue<V,T> {

    public SimpleParsedValue(V value, StyleConverter<V, T> converter) {
        super(value, converter);
    }
    
}
