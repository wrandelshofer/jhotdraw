/*
 * @(#)NullableCssSizeStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * NullableCssSizeStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class NullableCssSizeStyleableKey extends AbstractStyleableKey<CssSize>
        implements WriteableStyleableMapAccessor<CssSize> {

    static final long serialVersionUID = 1L;

    private final Converter<CssSize> converter = new CssSizeConverter(true);


    /**
     * Creates a new instance with the specified name, mask and default value.
     *  @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public NullableCssSizeStyleableKey(@NonNull String name, @Nullable CssSize defaultValue) {
        super(null, name, name, CssSize.class, true, defaultValue);
    }


    @Override
    public @NonNull Converter<CssSize> getCssConverter() {
        return converter;
    }

}
