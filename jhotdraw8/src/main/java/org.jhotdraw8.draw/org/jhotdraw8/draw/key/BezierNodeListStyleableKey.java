/*
 * @(#)BezierNodeListStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssBezierNodeListConverter;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * BezierNodeListStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class BezierNodeListStyleableKey
        extends AbstractStyleableKey<@NonNull ImmutableList<BezierNode>>
        implements WritableStyleableMapAccessor<@NonNull ImmutableList<BezierNode>>,
        NonNullMapAccessor<@NonNull ImmutableList<BezierNode>> {

    private static final long serialVersionUID = 1L;


    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public BezierNodeListStyleableKey(@NonNull String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *  @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public BezierNodeListStyleableKey(@NonNull String name, ImmutableList<BezierNode> defaultValue) {
        super(name, new TypeToken<ImmutableList<BezierNode>>() {
        }, defaultValue);

    }

    private final Converter<ImmutableList<BezierNode>> converter = new CssBezierNodeListConverter(false);

    @Override
    public @NonNull Converter<ImmutableList<BezierNode>> getCssConverter() {
        return converter;
    }

}
