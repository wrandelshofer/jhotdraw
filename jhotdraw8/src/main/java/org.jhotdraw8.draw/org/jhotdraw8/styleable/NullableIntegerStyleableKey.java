/*
 * @(#)NullableIntegerStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import org.jhotdraw8.css.text.CssIntegerConverter;
import org.jhotdraw8.text.Converter;

/**
 * IntegerStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class NullableIntegerStyleableKey extends SimpleStyleableKey<Integer> implements WriteableStyleableMapAccessor<Integer> {

    private static final long serialVersionUID = 1L;

    public NullableIntegerStyleableKey(String key) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key));
    }

    public NullableIntegerStyleableKey(String key, String cssName) {
        this(key, cssName, new CssIntegerConverter(true));
    }

    public NullableIntegerStyleableKey(String key, String cssName, Converter<Integer> converter) {
        super(key, Integer.class, null, converter);
    }

}
