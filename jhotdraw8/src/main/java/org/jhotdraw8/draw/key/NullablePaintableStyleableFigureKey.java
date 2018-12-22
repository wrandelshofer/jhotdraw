/* @(#)PaintStyleableFigureKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.Nonnull;

import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssPaintableConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * PaintStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NullablePaintableStyleableFigureKey extends AbstractStyleableFigureKey<Paintable> implements WriteableStyleableMapAccessor<Paintable> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, Paintable> cssMetaData;
    private Converter<Paintable> converter;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public NullablePaintableStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public NullablePaintableStyleableFigureKey(String name, Paintable defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name. type parameters are given. Otherwise
     * specify them in arrow brackets.
     * @param mask Dirty bit mask.
     * @param defaultValue The default value.
     */
    public NullablePaintableStyleableFigureKey(String key, DirtyMask mask, Paintable defaultValue) {
        super(key, Paintable.class, mask, defaultValue);

        Function<Styleable, StyleableProperty<Paintable>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        converter = new CssPaintableConverter(true);
        CssMetaData<Styleable, Paintable> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), defaultValue, inherits);
        cssMetaData = md;
    }

    @Nonnull
    @Override
    public CssMetaData<?, Paintable> getCssMetaData() {
        return cssMetaData;

    }

    @Override
    public Converter<Paintable> getConverter() {
        return converter;
    }
}
