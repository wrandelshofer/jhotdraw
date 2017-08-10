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
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssSize;
import org.jhotdraw8.text.CssSizeInsets;
import org.jhotdraw8.text.CssSizeInsetsConverter;
import org.jhotdraw8.text.StyleConverterAdapter;

/**
 * InsetsStyleableMapAccessor.
 *
 * @author werni
 */
public class SizeInsetsStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<CssSizeInsets> {

    private final static long serialVersionUID = 1L;

    private final CssMetaData<?, CssSizeInsets> cssMetaData;
    private final MapAccessor<CssSize> topKey;
    private final MapAccessor<CssSize> rightKey;
    private final MapAccessor<CssSize> bottomKey;
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
    public SizeInsetsStyleableMapAccessor(String name, MapAccessor<CssSize> topKey, MapAccessor<CssSize> rightKey, MapAccessor<CssSize> bottomKey, MapAccessor<CssSize> leftKey) {
        super(name, CssSizeInsets.class, new MapAccessor<?>[]{topKey, rightKey, bottomKey, leftKey}, new CssSizeInsets(topKey.getDefaultValue(), rightKey.getDefaultValue(), bottomKey.getDefaultValue(), leftKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<CssSizeInsets>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, CssSizeInsets> cnvrtr
                = new StyleConverterAdapter<>(getConverter());
        CssMetaData<Styleable, CssSizeInsets> md
                = new SimpleCssMetaData<>(property, function,
                        cnvrtr, getDefaultValue(), inherits);
        cssMetaData = md;

        this.topKey = topKey;
        this.rightKey = rightKey;
        this.bottomKey = bottomKey;
        this.leftKey = leftKey;
    }

    @Override
    public CssMetaData<?, CssSizeInsets> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<CssSizeInsets> converter;

    @Override
    public Converter<CssSizeInsets> getConverter() {
        if (converter == null) {
            converter = new CssSizeInsetsConverter();
        }
        return converter;
    }

    @Override
    public CssSizeInsets get(Map<? super Key<?>, Object> a) {
        return new CssSizeInsets(topKey.get(a), rightKey.get(a), bottomKey.get(a), leftKey.get(a));
    }

    @Override
    public CssSizeInsets put(Map<? super Key<?>, Object> a, CssSizeInsets value) {
        CssSizeInsets oldValue = get(a);
        topKey.put(a, value.getTop());
        rightKey.put(a, value.getRight());
        bottomKey.put(a, value.getBottom());
        leftKey.put(a, value.getLeft());
        return oldValue;
    }

    @Override
    public CssSizeInsets remove(Map<? super Key<?>, Object> a) {
        CssSizeInsets oldValue = get(a);
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
