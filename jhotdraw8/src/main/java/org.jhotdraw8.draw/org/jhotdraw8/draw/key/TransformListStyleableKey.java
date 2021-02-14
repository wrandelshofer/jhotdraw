/*
 * @(#)TransformListStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.CssTransformConverter;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * TransformListStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class TransformListStyleableKey extends AbstractStyleableKey<ImmutableList<Transform>>
        implements WriteableStyleableMapAccessor<ImmutableList<Transform>>, NonNullMapAccessor<ImmutableList<Transform>> {

    private static final long serialVersionUID = 1L;

    private Converter<ImmutableList<Transform>> converter;

    /**
     * Creates a new instance with the specified name and with an empty list as the
     * default value.
     *
     * @param name The name of the key.
     */
    public TransformListStyleableKey(@NonNull String name) {
        this(name, ImmutableLists.emptyList());
    }

    /**
     * Creates a new instance with the specified name, and default value.
     *  @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public TransformListStyleableKey(@NonNull String name, ImmutableList<Transform> defaultValue) {
        super(name, new TypeToken<ImmutableList<Transform>>() {
        }, defaultValue);
        converter = new CssListConverter<>(new CssTransformConverter());
    }

    @Override
    public @NonNull Converter<ImmutableList<Transform>> getCssConverter() {
        return converter;
    }

}
