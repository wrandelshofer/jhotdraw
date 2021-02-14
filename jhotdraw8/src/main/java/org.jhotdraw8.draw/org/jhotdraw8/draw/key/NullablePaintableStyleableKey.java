/*
 * @(#)NullablePaintableStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.text.CssPaintableConverter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * PaintStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class NullablePaintableStyleableKey extends AbstractStyleableKey<Paintable> implements WriteableStyleableMapAccessor<Paintable> {

    private static final long serialVersionUID = 1L;

    private Converter<Paintable> converter;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public NullablePaintableStyleableKey(@NonNull String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *  @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public NullablePaintableStyleableKey(@NonNull String key, Paintable defaultValue) {
        super(key, Paintable.class, defaultValue);

        converter = new CssPaintableConverter(true);
    }

    @Override
    public @NonNull Converter<Paintable> getCssConverter() {
        return converter;
    }
}
