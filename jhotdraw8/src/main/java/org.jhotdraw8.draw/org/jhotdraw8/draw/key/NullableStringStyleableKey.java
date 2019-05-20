/* @(#)NullableStringStyleableKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.text.CssStringConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.function.Function;

/**
 * NullableStringStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NullableStringStyleableKey extends AbstractStyleableKey<String>
        implements WriteableStyleableMapAccessor<String>, NonnullMapAccessor<String> {

    final static long serialVersionUID = 1L;
    @Nonnull
    private final CssMetaData<? extends Styleable, String> cssMetaData;

    /**
     * Creates a new instance with the specified name and with a null String
     * as the default value.
     *
     * @param name The name of the key.
     */
    public NullableStringStyleableKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public NullableStringStyleableKey(String name, String defaultValue) {
        this(name, defaultValue, null);
    }


    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     * @param helpText     the help text
     */
    public NullableStringStyleableKey(String name, String defaultValue, String helpText) {
        this(null, name, defaultValue, helpText);
    }

    public NullableStringStyleableKey(String namespace, String name, String defaultValue, String helpText) {
        super(namespace, name, String.class, true, defaultValue);
        converter = new CssStringConverter(true, '\'', helpText);
        Function<Styleable, StyleableProperty<String>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, String> converter = new StyleConverterAdapter<>(this.converter);
        CssMetaData<Styleable, String> md
                = new SimpleCssMetaData<>(property, function,
                converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Nonnull
    @Override
    public CssMetaData<? extends Styleable, String> getCssMetaData() {
        return cssMetaData;

    }

    @Nonnull
    private final CssStringConverter converter;

    @Nonnull
    @Override
    public Converter<String> getConverter() {
        return converter;
    }
}
