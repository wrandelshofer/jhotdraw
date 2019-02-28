/* @(#)NullableEnumStyleableFigureKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.styleable;

import javafx.css.CssMetaData;
import javafx.css.StyleablePropertyFactory;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;

/**
 * NullableEnumStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EnumStyleableKey<T extends Enum<T>> extends SimpleStyleableKey<T> implements WriteableStyleableMapAccessor<T> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, T> cssMetaData;

    private final boolean nullable;

    /**
     * Creates a new instance with the specified name, enum class, mask and with
     * null as the default value.
     *
     * @param name  The name of the key.
     * @param clazz The enum class.
     */
    public EnumStyleableKey(String name, Class<T> clazz) {
        this(name, clazz, true, null);
    }

    /**
     * Creates a new instance with the specified name, enum class, mask and
     * default value.
     *
     * @param name         The name of the key.
     * @param clazz        The enum class.
     * @param nullable     Whether the value is nullable
     * @param defaultValue The default value.
     */
    public EnumStyleableKey(String name, Class<T> clazz, boolean nullable, @Nullable T defaultValue) {
        super(name, clazz, null, null, defaultValue);

        this.nullable = nullable;

        if (!nullable && defaultValue == null) {
            throw new IllegalArgumentException("defaultValue may only be null if nullable=true");
        }

        converter = new CssEnumConverter<>(getValueType(), nullable);
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
