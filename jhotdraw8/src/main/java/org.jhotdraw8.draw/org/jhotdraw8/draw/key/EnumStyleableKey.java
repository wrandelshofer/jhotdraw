/*
 * @(#)EnumStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleablePropertyFactory;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * NullableEnumStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EnumStyleableKey<T extends Enum<T>> extends AbstractStyleableKey<T>
        implements WriteableStyleableMapAccessor<T>, NonnullMapAccessor<T> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, T> cssMetaData;

    /**
     * Creates a new instance with the specified name, enum class, mask and
     * default value.
     *
     * @param name         The name of the key.
     * @param clazz        The enum class.
     * @param mask         The mask.
     * @param defaultValue The default value.
     */
    public EnumStyleableKey(@Nonnull String name, @Nonnull Class<T> clazz, @Nonnull DirtyMask mask, @Nonnull T defaultValue) {
        super(name, clazz, defaultValue);

        if (defaultValue == null) {
            throw new IllegalArgumentException("defaultValue may not be null");
        }

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

    private final Converter<T> converter;

    @Override
    public Converter<T> getConverter() {
        return converter;
    }
}
