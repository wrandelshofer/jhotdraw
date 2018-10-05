/* @(#)IntegerStyleableKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.draw.key.SimpleCssMetaData;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.text.XmlIntegerConverter;

/**
 * IntegerStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class IntegerStyleableKey extends SimpleStyleableKey<Integer> {

    private final static long serialVersionUID = 1L;

    public IntegerStyleableKey(String key) {
        this(key, ReadOnlyStyleableMapAccessor.toCssName(key));
    }

    public IntegerStyleableKey(String key, String cssName) {
        super(key, Integer.class, null, new XmlIntegerConverter());
        
        Function<Styleable, StyleableProperty<Integer>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        final StyleConverter<String, Integer> converter
                = new StyleConverterAdapter<>(new XmlIntegerConverter());
        CssMetaData<Styleable, Integer> md
                = new SimpleCssMetaData<>(cssName, function,
                converter, 0, false);
        
       setCssMetaData(md);
    }

}
