/*
 * @(#)EnumStyleableKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;

/**
 * Nullable EnumStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class EnumStyleableKey<@Nullable T extends Enum<T>> extends SimpleStyleableKey<@Nullable T>
        implements WriteableStyleableMapAccessor<@Nullable T>, NonNullMapAccessor<@Nullable T> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, T> cssMetaData;


    /**
     * Creates a new instance with the specified name, enum class, mask and
     * default value.
     *
     * @param name         The name of the key.
     * @param clazz        The enum class.
     * @param defaultValue The default value.
     */
    public EnumStyleableKey(@NonNull String name, @NonNull Class<T> clazz, @Nullable T defaultValue) {
        super(name, clazz, null, null, defaultValue);

        converter = new CssEnumConverter<>(getValueType(), false);
        StyleablePropertyFactory<?> factory = new StyleablePropertyFactory<>(null);
        cssMetaData = factory.createEnumCssMetaData(clazz,
                Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, T> getCssMetaData() {
        return cssMetaData;

    }

    @NonNull
    private final Converter<T> converter;

    @NonNull
    @Override
    public Converter<T> getConverter() {
        return converter;
    }
}
