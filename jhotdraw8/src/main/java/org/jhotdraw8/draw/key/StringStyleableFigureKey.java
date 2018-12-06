/* @(#)StringStyleableFigureKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javax.annotation.Nonnull;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssStringConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * StringStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StringStyleableFigureKey extends AbstractStyleableFigureKey<String> implements WriteableStyleableMapAccessor<String> {

    final static long serialVersionUID = 1L;
    @Nonnull
    private final CssMetaData<? extends Styleable, String> cssMetaData;

    /**
     * Creates a new instance with the specified name and with an empty String
     * as the default value.
     *
     * @param name The name of the key.
     */
    public StringStyleableFigureKey(String name) {
        this(name, "");
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public StringStyleableFigureKey(String name, String defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue, null);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public StringStyleableFigureKey(String name, DirtyMask mask, String defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue, null);
    }
    public StringStyleableFigureKey(String name, DirtyMask mask, boolean nullable, String defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), nullable, defaultValue, null);
    }
    public StringStyleableFigureKey(String name, DirtyMask mask, String defaultValue, String helpText) {
        this(name, DirtyMask.of(DirtyBits.NODE), false, defaultValue, helpText);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     * @param helpText the help text
     */
    public StringStyleableFigureKey(String name, DirtyMask mask,boolean nullable, String defaultValue, String helpText) {
        super(name, String.class, nullable, mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
         cssMetaData = factory.createSizeCssMetaData(
         Figure.JHOTDRAW_CSS_PREFIX + getCssName(), s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         });*/
        converter = new CssStringConverter(false,'\'', helpText);
        Function<Styleable, StyleableProperty<String>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, String> converter  = new StyleConverterAdapter<>(this.converter);
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
