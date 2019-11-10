/*
 * @(#)Point2DStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.Point2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * Point2DStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class Point2DStyleableKey extends AbstractStyleableKey<Point2D> implements WriteableStyleableMapAccessor<Point2D> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, Point2D> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public Point2DStyleableKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public Point2DStyleableKey(String name, Point2D defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param mask         Dirty bit mask.
     * @param defaultValue The default value.
     */
    public Point2DStyleableKey(String key, DirtyMask mask, Point2D defaultValue) {
        super(key, Point2D.class, defaultValue);

        Function<Styleable, StyleableProperty<Point2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        CssMetaData<Styleable, Point2D> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), defaultValue, inherits);
        cssMetaData = md;
    }

    @NonNull
    @Override
    public CssMetaData<?, Point2D> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<Point2D> converter = new Point2DConverter(false);

    @NonNull
    @Override
    public Converter<Point2D> getConverter() {
        return converter;
    }
}
