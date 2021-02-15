/*
 * @(#)WordListStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.xml.text.XmlWordListConverter;

/**
 * WordListStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class WordListStyleableKey extends AbstractStyleableKey<ImmutableList<String>> implements WritableStyleableMapAccessor<ImmutableList<String>> {

    private static final long serialVersionUID = 1L;
    private Converter<ImmutableList<String>> converter = new XmlWordListConverter();

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public WordListStyleableKey(@NonNull String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public WordListStyleableKey(@NonNull String name, ImmutableList<String> defaultValue) {
        super(name, new TypeToken<ImmutableList<String>>() {
        }, defaultValue);
    }

    @Override
    public @NonNull Converter<ImmutableList<String>> getCssConverter() {
        return converter;
    }

}
