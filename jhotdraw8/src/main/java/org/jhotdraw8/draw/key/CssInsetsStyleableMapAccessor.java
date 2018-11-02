/* @(#)InsetsStyleableMapAccessor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.Map;
import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssInsets;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssInsetsConverter;
import org.jhotdraw8.text.StyleConverterAdapter;

/**
 * InsetsStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssInsetsStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<CssInsets> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, CssInsets> cssMetaData;
    @Nonnull
    private final MapAccessor<CssSize> topKey;
    @Nonnull
    private final MapAccessor<CssSize> rightKey;
    @Nonnull
    private final MapAccessor<CssSize> bottomKey;
    @Nonnull
    private final MapAccessor<CssSize> leftKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param topKey the insets top key
     * @param rightKey the insets right key
     * @param bottomKey the insets bottom key
     * @param leftKey the insets left key
     */
    public CssInsetsStyleableMapAccessor(String name, MapAccessor<CssSize> topKey, MapAccessor<CssSize> rightKey, MapAccessor<CssSize> bottomKey, MapAccessor<CssSize> leftKey) {
        super(name, CssInsets.class, new MapAccessor<?>[]{topKey, rightKey, bottomKey, leftKey}, new CssInsets(topKey.getDefaultValue(), rightKey.getDefaultValue(), bottomKey.getDefaultValue(), leftKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<CssInsets>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, CssInsets> cnvrtr
                = new StyleConverterAdapter<>(getConverter());
        CssMetaData<Styleable, CssInsets> md
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
    public CssMetaData<?, CssInsets> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<CssInsets> converter= new CssInsetsConverter(false);

    @Override
    public Converter<CssInsets> getConverter() {
        return converter;
    }

    @Nullable
    @Override
    public CssInsets get(Map<? super Key<?>, Object> a) {
        final CssSize top = topKey.get(a);
        final CssSize right = rightKey.get(a);
        final CssSize bottom = bottomKey.get(a);
        final CssSize left = leftKey.get(a);
        if (top == null || right == null || bottom == null | left == null) {
            return null;
        }
        return new CssInsets(
                top == null ? CssSize.ZERO : top,
                right == null ? CssSize.ZERO : right,
                bottom == null ? CssSize.ZERO : bottom,
                left == null ? CssSize.ZERO : left
        );
    }

    @Nullable
    @Override
    public CssInsets put(Map<? super Key<?>, Object> a, @Nullable CssInsets value) {
        CssInsets oldValue = get(a);
        if (value == null) {
            topKey.put(a, null);
            rightKey.put(a, null);
            bottomKey.put(a, null);
            leftKey.put(a, null);
        } else {
            topKey.put(a, value.getTop());
            rightKey.put(a, value.getRight());
            bottomKey.put(a, value.getBottom());
            leftKey.put(a, value.getLeft());
        }
        return oldValue;
    }

    @Nullable
    @Override
    public CssInsets remove(Map<? super Key<?>, Object> a) {
        CssInsets oldValue = get(a);
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
