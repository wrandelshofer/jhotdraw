/*
 * @(#)EnumStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleablePropertyFactory;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

import java.util.Objects;

/**
 * NullableEnumStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class EnumStyleableKey<T extends Enum<T>> extends AbstractStyleableKey<T>
        implements WriteableStyleableMapAccessor<T>, NonNullMapAccessor<T> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, T> cssMetaData;

    /**
     * Creates a new instance with the specified name, enum class, mask and
     * default value.
     *  @param name         The name of the key.
     * @param clazz        The enum class.
     * @param defaultValue The default value.
     */
    public EnumStyleableKey(@NonNull String name, @NonNull Class<T> clazz, @NonNull T defaultValue) {
        super(name, clazz, defaultValue);

        Objects.requireNonNull(defaultValue, "defaultValue is null");

        StyleablePropertyFactory<?> factory = new StyleablePropertyFactory<>(null);
        converter = new CssEnumConverter<>(getValueType(), false);
        cssMetaData = factory.createEnumCssMetaData(clazz,
                Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public CssMetaData<?, T> getCssMetaData() {
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
