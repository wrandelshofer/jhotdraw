/*
 * @(#)CssSizeStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * CssSizeStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class CssSizeStyleableKey extends AbstractStyleableKey<@NonNull CssSize> implements WritableStyleableMapAccessor<@NonNull CssSize>,
        NonNullMapAccessor<@NonNull CssSize> {

    static final long serialVersionUID = 1L;

    private final Converter<@NonNull CssSize> converter = new CssSizeConverter(false);


    /**
     * Creates a new instance with the specified name, mask and default value.
     *  @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public CssSizeStyleableKey(String name, @NonNull CssSize defaultValue) {
        super(name, CssSize.class, defaultValue);

    }


    @Override
    public @NonNull Converter<CssSize> getCssConverter() {
        return converter;
    }

}
