/*
 * @(#)ListStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * TListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 */
public class ListStyleableKey<T> extends AbstractStyleableKey<ImmutableList<T>>
        implements WriteableStyleableMapAccessor<ImmutableList<T>>,
        NonNullMapAccessor<ImmutableList<T>> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, ImmutableList<T>> cssMetaData;
    private Converter<ImmutableList<T>> converter;

    /**
     * Creates a new instance with the specified name and with an empty list as the
     * default value.
     *
     * @param name      The name of the key.
     * @param clazz     the class of the type
     * @param converter String converter for a list element
     */
    public ListStyleableKey(@NonNull String name, @NonNull Class<T> clazz, @NonNull CssConverter<T> converter) {
        this(name, clazz, converter, ImmutableLists.emptyList());
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *  @param name         The name of the key.
     * @param clazz        the class of the type
     * @param converter    String converter for a list element
     * @param defaultValue The default value.
     */
    public ListStyleableKey(@NonNull String name, @NonNull Class<T> clazz, @NonNull CssConverter<T> converter, @NonNull ImmutableList<T> defaultValue) {
        super(name, ImmutableList.class, new Class<?>[]{clazz}, defaultValue);

        Function<Styleable, StyleableProperty<ImmutableList<T>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        this.converter = new CssListConverter<>(converter);
        CssMetaData<Styleable, ImmutableList<T>> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(this.converter), defaultValue, inherits);
        cssMetaData = md;
    }

    @NonNull
    @Override
    public CssMetaData<?, ImmutableList<T>> getCssMetaData() {
        return cssMetaData;
    }

    @Override
    public Converter<ImmutableList<T>> getConverter() {
        return converter;
    }

}
