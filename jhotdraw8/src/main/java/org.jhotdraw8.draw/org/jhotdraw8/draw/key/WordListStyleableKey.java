/* @(#)WordListStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.text.CssWordListConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * WordListStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class WordListStyleableKey extends AbstractStyleableKey<ImmutableList<String>> implements WriteableStyleableMapAccessor<ImmutableList<String>> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, ImmutableList<String>> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public WordListStyleableKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public WordListStyleableKey(String name, ImmutableList<String> defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param mask         The dirty mask.
     * @param defaultValue The default value.
     */
    public WordListStyleableKey(String name, DirtyMask mask, ImmutableList<String> defaultValue) {
        super(name, ImmutableList.class, new Class<?>[]{String.class}, defaultValue);
        Function<Styleable, StyleableProperty<ImmutableList<String>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, ImmutableList<String>> converter
                = new StyleConverterAdapter<>(new CssWordListConverter());
        CssMetaData<Styleable, ImmutableList<String>> md
                = new SimpleCssMetaData<>(property, function,
                converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Nonnull
    @Override
    public CssMetaData<?, ImmutableList<String>> getCssMetaData() {
        return cssMetaData;
    }

    private Converter<ImmutableList<String>> converter;

    @Override
    public Converter<ImmutableList<String>> getConverter() {
        if (converter == null) {
            converter = new CssWordListConverter();
        }
        return converter;
    }

}
