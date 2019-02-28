/* @(#)InsetsStyleableMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.text.CssInsetsConverterOLD;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.function.Function;

/**
 * InsetsStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class InsetsStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<Insets> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, Insets> cssMetaData;
    @Nonnull
    private final MapAccessor<Double> topKey;
    @Nonnull
    private final MapAccessor<Double> rightKey;
    @Nonnull
    private final MapAccessor<Double> bottomKey;
    @Nonnull
    private final MapAccessor<Double> leftKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name      the name of the accessor
     * @param topKey    the insets top key
     * @param rightKey  the insets right key
     * @param bottomKey the insets bottom key
     * @param leftKey   the insets left key
     */
    public InsetsStyleableMapAccessor(String name, MapAccessor<Double> topKey, MapAccessor<Double> rightKey, MapAccessor<Double> bottomKey, MapAccessor<Double> leftKey) {
        super(name, Insets.class, new MapAccessor<?>[]{topKey, rightKey, bottomKey, leftKey}, new Insets(topKey.getDefaultValue(), rightKey.getDefaultValue(), bottomKey.getDefaultValue(), leftKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<Insets>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, Insets> cnvrtr
                = new StyleConverterAdapter<>(getConverter());
        CssMetaData<Styleable, Insets> md
                = new SimpleCssMetaData<>(property, function,
                cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.topKey = topKey;
        this.rightKey = rightKey;
        this.bottomKey = bottomKey;
        this.leftKey = leftKey;
    }

    @Nonnull
    @Override
    public CssMetaData<?, Insets> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<Insets> converter;

    @Override
    public Converter<Insets> getConverter() {
        if (converter == null) {
            converter = new CssInsetsConverterOLD(false);
        }
        return converter;
    }

    @Nonnull
    @Override
    public Insets get(@Nonnull Map<? super Key<?>, Object> a) {
        final Double top = topKey.get(a);
        final Double right = rightKey.get(a);
        final Double bottom = bottomKey.get(a);
        final Double left = leftKey.get(a);
        return new Insets(
                top == null ? 0.0 : top,
                right == null ? 0.0 : right,
                bottom == null ? 0.0 : bottom,
                left == null ? 0.0 : left
        );
    }

    @Nonnull
    @Override
    public Insets put(@Nonnull Map<? super Key<?>, Object> a, @Nonnull Insets value) {
        Insets oldValue = get(a);
        topKey.put(a, value.getTop());
        rightKey.put(a, value.getRight());
        bottomKey.put(a, value.getBottom());
        leftKey.put(a, value.getLeft());
        return oldValue;
    }

    @Nonnull
    @Override
    public Insets remove(@Nonnull Map<? super Key<?>, Object> a) {
        Insets oldValue = get(a);
        topKey.remove(a);
        rightKey.remove(a);
        bottomKey.remove(a);
        leftKey.remove(a);
        return oldValue;
    }

    @Override
    public boolean isNullable() {
        return false;
    }
}
