/*
 * @(#)Point2DListStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.Point2DConverter;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * Point2DListStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class Point2DListStyleableKey extends AbstractStyleableKey<@NonNull ImmutableList<@NonNull Point2D>>
        implements WritableStyleableMapAccessor<@NonNull ImmutableList<@NonNull Point2D>>, NonNullMapAccessor<ImmutableList<@NonNull Point2D>> {

    private static final long serialVersionUID = 1L;

    private final @NonNull Converter<ImmutableList<@NonNull Point2D>> converter;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public Point2DListStyleableKey(@NonNull String name) {
        this(name, ImmutableLists.emptyList());
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public Point2DListStyleableKey(@NonNull String name, @NonNull ImmutableList<@NonNull Point2D> defaultValue) {
        super(name, new TypeToken<ImmutableList<Point2D>>() {
        }, defaultValue);

        this.converter = new CssListConverter<>(
                new Point2DConverter(false, false), " ,");
    }

    @Override
    public @NonNull Converter<ImmutableList<Point2D>> getCssConverter() {
        return converter;
    }

}
