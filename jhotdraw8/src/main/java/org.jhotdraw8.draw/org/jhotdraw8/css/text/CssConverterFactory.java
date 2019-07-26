/*
 * @(#)CssConverterFactory.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.ConverterFactory;
import org.jhotdraw8.text.DefaultConverter;

/**
 * CssConverterFactory.
 *
 * @author Werner Randelshofer
 */
public class CssConverterFactory implements ConverterFactory {

    @Nonnull
    @Override
    public Converter<?> apply(@Nullable String type, String style) {
        if (type == null) {
            return new DefaultConverter();
        }
        switch (type) {
            case "number":
                return new CssNumberConverter(false);
            case "size":
                return new CssSizeConverter(false);
            case "word":
                return new CssWordConverter();
            case "paint":
                return new CssPaintableConverter(false);
            case "font":
                return new CssFontConverter(false);
            default:
                throw new IllegalArgumentException("illegal type:" + type);
        }
    }

}
