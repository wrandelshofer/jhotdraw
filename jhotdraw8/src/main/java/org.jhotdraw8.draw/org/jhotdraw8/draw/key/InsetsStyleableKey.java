/*
 * @(#)InsetsStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.geometry.Insets;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.InsetsConverter;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * InsetsStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class InsetsStyleableKey extends AbstractStyleableKey<Insets> implements WritableStyleableMapAccessor<Insets> {

    private static final long serialVersionUID = 1L;
    private final Converter<Insets> converter = new InsetsConverter(false);

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public InsetsStyleableKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *  @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public InsetsStyleableKey(String key, Insets defaultValue) {
        super(key, Insets.class, defaultValue);

    }

    @Override
    public @NonNull Converter<Insets> getCssConverter() {
        return converter;
    }
}
