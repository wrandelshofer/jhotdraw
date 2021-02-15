/*
 * @(#)RegexStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.CssRegexConverter;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.RegexReplace;

/**
 * RegexStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class RegexStyleableKey extends AbstractStyleableKey<RegexReplace> implements WritableStyleableMapAccessor<RegexReplace> {

    static final long serialVersionUID = 1L;
    private final @NonNull CssRegexConverter converter;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public RegexStyleableKey(String name) {
        this(name, new RegexReplace());
    }


    /**
     * Creates a new instance with the specified name and default value. The
     * value is nullable.
     *  @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public RegexStyleableKey(String name, RegexReplace defaultValue) {
        this(name, true, defaultValue);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *  @param name         The name of the key.
     * @param nullable     whether the value is nullable
     * @param defaultValue The default value.
     */
    public RegexStyleableKey(String name, boolean nullable, RegexReplace defaultValue) {
        super(null, name, RegexReplace.class, nullable, defaultValue);
        converter = new CssRegexConverter(isNullable());
    }

    @Override
    public @NonNull Converter<RegexReplace> getCssConverter() {
        return converter;
    }
}
