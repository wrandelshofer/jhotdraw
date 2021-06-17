/*
 * @(#)NullableSvgPathStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.scene.shape.PathElement;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.text.CssFXSvgPathConverter;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

import java.util.List;

/**
 * NullableAwtSvgPathStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class NullableFXSvgPathStyleableKey extends AbstractStyleableKey<List<PathElement>> implements WritableStyleableMapAccessor<List<PathElement>> {

    private static final long serialVersionUID = 1L;

    private final @NonNull Converter<List<PathElement>> converter;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public NullableFXSvgPathStyleableKey(@NonNull String name) {
        this(name, null);
    }


    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public NullableFXSvgPathStyleableKey(@NonNull String key, @Nullable List<PathElement> defaultValue) {
        super(null, key, String.class, true, defaultValue);

        converter = new CssFXSvgPathConverter(isNullable());
    }

    @Override
    public @NonNull Converter<List<PathElement>> getCssConverter() {
        return converter;
    }
}
