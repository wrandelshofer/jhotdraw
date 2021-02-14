/*
 * @(#)FontStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.text.CssFontConverter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * FontStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class FontStyleableKey extends AbstractStyleableKey<CssFont> implements WriteableStyleableMapAccessor<CssFont> {

    private static final long serialVersionUID = 1L;

    private final Converter<CssFont> converter = new CssFontConverter(false);

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public FontStyleableKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public FontStyleableKey(String name, CssFont defaultValue) {
        super(name, CssFont.class, defaultValue);
    }

    @Override
    public @NonNull Converter<CssFont> getCssConverter() {
        return converter;
    }
}
