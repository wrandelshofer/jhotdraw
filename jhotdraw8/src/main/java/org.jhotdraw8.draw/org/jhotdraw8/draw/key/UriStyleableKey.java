/*
 * @(#)UriStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.CssUriConverter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

import java.net.URI;

/**
 * URIStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class UriStyleableKey extends AbstractStyleableKey<URI> implements WriteableStyleableMapAccessor<URI> {

    private static final long serialVersionUID = 1L;
    private Converter<URI> converter = new CssUriConverter();

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public UriStyleableKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *  @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public UriStyleableKey(String key, URI defaultValue) {
        super(key, URI.class, defaultValue);
    }

    @Override
    public @NonNull Converter<URI> getCssConverter() {
        return converter;
    }
}
