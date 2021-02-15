/*
 * @(#)ListStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * TListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class ListStyleableKey<T> extends AbstractStyleableKey<@NonNull ImmutableList<T>>
        implements WritableStyleableMapAccessor<@NonNull ImmutableList<T>>,
        NonNullMapAccessor<@NonNull ImmutableList<T>> {

    private static final long serialVersionUID = 1L;

    private @NonNull Converter<@NonNull ImmutableList<T>> converter;

    /**
     * Creates a new instance with the specified name and with an empty list as the
     * default value.
     *
     * @param name      The name of the key.
     * @param type      the class of the type
     * @param converter String converter for a list element
     */
    public ListStyleableKey(@NonNull String name, @NonNull TypeToken<ImmutableList<T>> type, @NonNull CssConverter<T> converter) {
        this(name, type, converter, ImmutableLists.emptyList());
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param type         the class of the type
     * @param converter    String converter for a list element
     * @param defaultValue The default value.
     */
    public ListStyleableKey(@NonNull String name, @NonNull TypeToken<ImmutableList<T>> type, @NonNull CssConverter<T> converter, @NonNull ImmutableList<T> defaultValue) {
        super(name, type, defaultValue);

        this.converter = new CssListConverter<>(converter);
    }

    @Override
    public @NonNull Converter<ImmutableList<T>> getCssConverter() {
        return converter;
    }

}
