/*
 * @(#)Point2DListStyleableKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.Point2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * Point2DListStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class Point2DListStyleableKey extends AbstractStyleableKey<@NonNull ImmutableList<@NonNull Point2D>>
        implements WriteableStyleableMapAccessor<@NonNull ImmutableList<@NonNull Point2D>>, NonNullMapAccessor<ImmutableList<@NonNull Point2D>> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, ImmutableList<@NonNull Point2D>> cssMetaData;
    @NonNull
    private final Converter<ImmutableList<@NonNull Point2D>> converter;

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
        super(name, ImmutableList.class, new Class<?>[]{Point2D.class}, defaultValue);

        Function<Styleable, StyleableProperty<ImmutableList<Point2D>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        this.converter = new CssListConverter<>(
                new Point2DConverter(false, false), " ,");
        CssMetaData<Styleable, ImmutableList<Point2D>> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(this.converter),
                defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, ImmutableList<Point2D>> getCssMetaData() {
        return cssMetaData;
    }

    @NonNull
    @Override
    public Converter<ImmutableList<Point2D>> getConverter() {
        return converter;
    }

}
