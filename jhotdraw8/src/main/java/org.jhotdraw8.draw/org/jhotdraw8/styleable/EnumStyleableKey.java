/*
 * @(#)EnumStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.StyleablePropertyFactory;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;

/**
 * Nullable EnumStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class EnumStyleableKey<T extends Enum<T>> extends SimpleStyleableKey<T>
        implements WriteableStyleableMapAccessor<T>, NonnullMapAccessor<T> {

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
    public EnumStyleableKey(String name, Class<T> clazz, @Nonnull T defaultValue) {
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
    public CssMetaData<?, T> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<T> converter;

    @Override
    public Converter<T> getConverter() {
        return converter;
    }
}
