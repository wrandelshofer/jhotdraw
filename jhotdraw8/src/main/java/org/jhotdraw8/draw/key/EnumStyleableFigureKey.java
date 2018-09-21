/* @(#)EnumStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javax.annotation.Nullable;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssEnumConverter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * EnumStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EnumStyleableFigureKey<T extends Enum<T>> extends AbstractStyleableFigureKey<T> implements WriteableStyleableMapAccessor<T> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, T> cssMetaData;

    private final boolean nullable;

    /**
     * Creates a new instance with the specified name, enum class, mask and with
     * null as the default value.
     *
     * @param name The name of the key.
     * @param clazz The enum class.
     * @param mask The mask.
     */
    public EnumStyleableFigureKey(String name, Class<T> clazz, DirtyMask mask) {
        this(name, clazz, mask, true, null);
    }

    /**
     * Creates a new instance with the specified name, enum class, mask and
     * default value.
     *
     * @param name The name of the key.
     * @param clazz The enum class.
     * @param mask The mask.
     * @param nullable Whether the value is nullable
     * @param defaultValue The default value.
     */
    public EnumStyleableFigureKey(String name, Class<T> clazz, DirtyMask mask, boolean nullable, @Nullable T defaultValue) {
        super(name, clazz, mask, defaultValue);

        this.nullable = nullable;

        if (!nullable && defaultValue == null) {
            throw new IllegalArgumentException("defaultValue may only be null if nullable=true");
        }

        StyleablePropertyFactory<?> factory = new StyleablePropertyFactory<Styleable>(null);
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

    private Converter<T> converter;

    @Override
    public Converter<T> getConverter() {
        if (converter == null) {
            converter = new CssEnumConverter<T>(getValueType(), nullable);
        }
        return converter;
    }
}
