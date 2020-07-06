/*
 * @(#)IntegerStyleableKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.css.text.CssIntegerConverter;
import org.jhotdraw8.draw.key.SimpleCssMetaData;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * IntegerStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class IntegerStyleableKey extends SimpleStyleableKey<Integer> implements WriteableStyleableMapAccessor<Integer> {

    private final static long serialVersionUID = 1L;

    public IntegerStyleableKey(String key) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key));
    }

    public IntegerStyleableKey(String key, String cssName) {
        this(key, cssName, new CssIntegerConverter(false));
    }

    public IntegerStyleableKey(String key, String cssName, Converter<Integer> converter) {
        super(key, Integer.class, null, converter);

        Function<Styleable, StyleableProperty<Integer>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        CssMetaData<Styleable, Integer> md
                = new SimpleCssMetaData<>(cssName, function,
                new StyleConverterAdapter<>(converter), 0, false);

        setCssMetaData(md);
    }

}
