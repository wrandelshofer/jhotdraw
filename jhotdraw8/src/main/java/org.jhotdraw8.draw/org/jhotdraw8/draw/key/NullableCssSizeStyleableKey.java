/*
 * @(#)NullableCssSizeStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * NullableCssSizeStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class NullableCssSizeStyleableKey extends AbstractStyleableKey<CssSize>
        implements WriteableStyleableMapAccessor<CssSize> {

    final static long serialVersionUID = 1L;

    private final Converter<CssSize> converter = new CssSizeConverter(true);
    @NonNull
    private final CssMetaData<? extends Styleable, CssSize> cssMetaData;


    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public NullableCssSizeStyleableKey(@NonNull String name, @Nullable CssSize defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }


    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param mask         The dirty mask.
     * @param defaultValue The default value.
     */
    public NullableCssSizeStyleableKey(@NonNull String name, DirtyMask mask, @Nullable CssSize defaultValue) {
        super(null, name, CssSize.class, true, defaultValue);
        Function<Styleable, StyleableProperty<CssSize>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, CssSize> cvrtr
                = new StyleConverterAdapter<>(converter);
        CssMetaData<Styleable, CssSize> md
                = new SimpleCssMetaData<>(property, function,
                cvrtr, defaultValue, inherits);
        cssMetaData = md;
    }


    @NonNull
    @Override
    public Converter<CssSize> getConverter() {
        return converter;
    }

    @NonNull
    @Override
    public CssMetaData<? extends Styleable, CssSize> getCssMetaData() {
        return cssMetaData;

    }
}
