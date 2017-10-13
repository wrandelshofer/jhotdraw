/* @(#)URIStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.net.URI;
import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssUriConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * URIStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class UriStyleableFigureKey extends AbstractStyleableFigureKey<URI> implements WriteableStyleableMapAccessor<URI> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, URI> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public UriStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public UriStyleableFigureKey(String name, URI defaultValue) {
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
    public UriStyleableFigureKey(String key, DirtyMask mask, URI defaultValue) {
        super(key, URI.class, mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
         cssMetaData = factory.createPoint2DCssMetaData(
         Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         });*/

        Function<Styleable, StyleableProperty<URI>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, URI> cnvrtr
                = new StyleConverterAdapter<>(getConverter());
        CssMetaData<Styleable, URI> md
                = new SimpleCssMetaData<>(property, function,
                        cnvrtr, defaultValue, inherits);
        cssMetaData = md;
    }

    @Override
    public CssMetaData<?, URI> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<URI> converter;

    @Override
    public Converter<URI> getConverter() {
        if (converter == null) {
            converter = new CssUriConverter();
        }
        return converter;
    }
}
