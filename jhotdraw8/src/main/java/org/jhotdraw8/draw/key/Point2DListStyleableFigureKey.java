/* @(#)DoubleListStyleableFigureKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Point2D;
import javax.annotation.Nonnull;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.text.Point2DConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * DoubleListStyleableFigureKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Point2DListStyleableFigureKey extends AbstractStyleableFigureKey<ImmutableList<Point2D>> implements WriteableStyleableMapAccessor<ImmutableList<Point2D>> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, ImmutableList<Point2D>> cssMetaData;
    private final Converter<ImmutableList<Point2D>> converter ;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public Point2DListStyleableFigureKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name The name of the key.
     * @param defaultValue The default value.
     */
    public Point2DListStyleableFigureKey(String name, ImmutableList<Point2D> defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name The name of the key.
     * @param mask The dirty mask.
     * @param defaultValue The default value.
     */
    public Point2DListStyleableFigureKey(String name, DirtyMask mask, ImmutableList<Point2D> defaultValue) {
        super(name, ImmutableList.class, new Class<?>[]{Point2D.class}, mask, defaultValue);

        Function<Styleable, StyleableProperty<ImmutableList<Point2D>>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        this.converter=new CssListConverter<>(
                new Point2DConverter(false, false)," ");
        CssMetaData<Styleable, ImmutableList<Point2D>> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(this.converter),
                defaultValue, inherits);
        cssMetaData = md;
    }

    @Nonnull
    @Override
    public CssMetaData<?, ImmutableList<Point2D>> getCssMetaData() {
        return cssMetaData;
    }

    @Override
    public Converter<ImmutableList<Point2D>> getConverter() {
        return converter;
    }

}
