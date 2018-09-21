/* @(#)Point2DStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Point2D;
import javax.annotation.Nonnull;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssPoint2DConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * Point2DStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Point2DStyleableFigureKey extends AbstractStyleableFigureKey<Point2D> implements WriteableStyleableMapAccessor<Point2D> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, Point2D> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public Point2DStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public Point2DStyleableFigureKey(String name, Point2D defaultValue) {
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
    public Point2DStyleableFigureKey(String key, DirtyMask mask, Point2D defaultValue) {
        super(key, Point2D.class, mask, defaultValue);
        /*
         StyleablePropertyFactory factory = new StyleablePropertyFactory(null);
         cssMetaData = factory.createPoint2DCssMetaData(
         Figure.JHOTDRAW_CSS_PREFIX + getName(), s -> {
         StyleablePropertyBean spb = (StyleablePropertyBean) s;
         return spb.getStyleableProperty(this);
         });*/

        Function<Styleable, StyleableProperty<Point2D>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Point2D> converter
                = new StyleConverterAdapter<Point2D>(new CssPoint2DConverter());
        CssMetaData<Styleable, Point2D> md
                = new SimpleCssMetaData<Styleable, Point2D>(property, function,
                        converter, defaultValue, inherits);
        cssMetaData = md;
    }

    @Nonnull
    @Override
    public CssMetaData<?, Point2D> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<Point2D> converter;

    @Override
    public Converter<Point2D> getConverter() {
        if (converter == null) {
            converter = new CssPoint2DConverter();
        }
        return converter;
    }
}
