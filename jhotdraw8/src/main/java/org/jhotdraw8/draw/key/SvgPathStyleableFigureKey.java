/* @(#)SvgPathStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.shape.SVGPath;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssSvgPathConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * SvgPathStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SvgPathStyleableFigureKey extends AbstractStyleableFigureKey<String> implements WriteableStyleableMapAccessor<String> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, String> cssMetaData;

    private Converter<String> converter;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public SvgPathStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public SvgPathStyleableFigureKey(String name, String defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name. type parameters are given. Otherwise
     * specify them in arrow brackets.
     * @param mask Dirty bit mask.
     * @param defaultValue The default value.
     */
    public SvgPathStyleableFigureKey(String key, DirtyMask mask, String defaultValue) {
        super(key, String.class, mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
         cssMetaData = factory.createSVGPathCssMetaData(
         Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         });*/

        Function<Styleable, StyleableProperty<String>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, String> converter
                = new StyleConverterAdapter<String>(getConverter());
        CssMetaData<Styleable, String> md
                = new SimpleCssMetaData<Styleable, String>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @NonNull
    @Override
    public CssMetaData<?, String> getCssMetaData() {
        return cssMetaData;

    }

    @Override
    public Converter<String> getConverter() {
        if (converter == null) {
            converter = new CssSvgPathConverter(isNullable());
        }
        return converter;
    }
}
