/*
 * @(#)Rectangle2DStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Rectangle2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.Rectangle2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * Rectangle2DStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class Rectangle2DStyleableKey extends AbstractStyleableKey<Rectangle2D> implements WriteableStyleableMapAccessor<Rectangle2D> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, Rectangle2D> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public Rectangle2DStyleableKey(@NonNull String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param key         The name of the key.
     * @param defaultValue The default value.
     */
    public Rectangle2DStyleableKey(@NonNull String key, Rectangle2D defaultValue) {
        super(key, Rectangle2D.class, defaultValue);

        Function<Styleable, StyleableProperty<Rectangle2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Rectangle2D> cnvrtr
                = new StyleConverterAdapter<>(getCssConverter());
        CssMetaData<Styleable, Rectangle2D> md
                = new SimpleCssMetaData<>(property, function,
                cnvrtr, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, Rectangle2D> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<Rectangle2D> converter;

    @Override
    public @NonNull Converter<Rectangle2D> getCssConverter() {
        if (converter == null) {
            converter = new Rectangle2DConverter(false);
        }
        return converter;
    }
}
