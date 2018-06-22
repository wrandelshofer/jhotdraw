/* @(#)FontStyleableMapAccessor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.Map;
import java.util.function.Function;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.CssFontConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.text.CssFont;

/**
 * FontStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FontStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<CssFont> {

    private final static long serialVersionUID = 1L;

    @NonNull
    private final CssMetaData<?, CssFont> cssMetaData;
    @NonNull
    private final MapAccessor<String> familyKey;
    @NonNull
    private final MapAccessor<FontWeight> weightKey;
    @NonNull
    private final MapAccessor<FontPosture> postureKey;
    @NonNull
    private final MapAccessor<Double> sizeKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name the name of the accessor
     * @param familyKey the font family key
     * @param weightKey the font weight key
     * @param postureKey the font posture key
     * @param sizeKey the font size key
     */
    public FontStyleableMapAccessor(String name, MapAccessor<String> familyKey, MapAccessor<FontWeight> weightKey, MapAccessor<FontPosture> postureKey, MapAccessor<Double> sizeKey) {
        super(name, CssFont.class, new MapAccessor<?>[]{familyKey, sizeKey, weightKey, postureKey}, CssFont.font(familyKey.getDefaultValue(), weightKey.getDefaultValue(), postureKey.getDefaultValue(), sizeKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<CssFont>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        final StyleConverter<String, CssFont> converter
                = new StyleConverterAdapter<CssFont>(new CssFontConverter());
        CssMetaData<Styleable, CssFont> md
                = new SimpleCssMetaData<Styleable, CssFont>(property, function,
                        converter, getDefaultValue(), inherits);
        cssMetaData = md;

        this.familyKey = familyKey;
        this.sizeKey = sizeKey;
        this.weightKey = weightKey;
        this.postureKey = postureKey;
    }

    @NonNull
    @Override
    public CssMetaData<?, CssFont> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<CssFont> converter;

    @Override
    public Converter<CssFont> getConverter() {
        if (converter == null) {
            converter = new CssFontConverter();
        }
        return converter;
    }

    @Override
    public CssFont get(Map<? super Key<?>, Object> a) {
        CssFont f = CssFont.font(familyKey.get(a), weightKey.get(a), postureKey.get(a), sizeKey.get(a));
        return f;
    }

    @Override
    public CssFont put(Map<? super Key<?>, Object> a, @NonNull CssFont value) {
        CssFont oldValue = get(a);
        familyKey.put(a, value.getFamily());
        weightKey.put(a, value.getWeight());
        postureKey.put(a, value.getPosture());
        sizeKey.put(a, value.getSize());
        return oldValue;
    }

    @Override
    public CssFont remove(Map<? super Key<?>, Object> a) {
        CssFont oldValue = get(a);
        familyKey.remove(a);
        weightKey.remove(a);
        postureKey.remove(a);
        sizeKey.remove(a);
        return oldValue;
    }

    @Override
    public boolean isNullable() {
        return false;
    }
}
