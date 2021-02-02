/*
 * @(#)BezierNodeListStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssBezierNodeListConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * BezierNodeListStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class BezierNodeListStyleableKey
        extends AbstractStyleableKey<@NonNull ImmutableList<BezierNode>>
        implements WriteableStyleableMapAccessor<@NonNull ImmutableList<BezierNode>>,
        NonNullMapAccessor<@NonNull ImmutableList<BezierNode>> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, ImmutableList<BezierNode>> cssMetaData;

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

        Function<Styleable, StyleableProperty<ImmutableList<BezierNode>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        CssMetaData<Styleable, ImmutableList<BezierNode>> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, @NonNull ImmutableList<BezierNode>> getCssMetaData() {
        return cssMetaData;
    }

    private final Converter<ImmutableList<BezierNode>> converter = new CssBezierNodeListConverter(false);

    @NonNull
    @Override
    public Converter<ImmutableList<BezierNode>> getCssConverter() {
        return converter;
    }

}
