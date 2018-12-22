/* @(#)FontStyleableMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.Map;
import java.util.function.Function;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssFontConverter;
import org.jhotdraw8.text.StyleConverterAdapter;
import org.jhotdraw8.css.CssFont;

/**
 * FontStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FontStyleableMapAccessor extends AbstractStyleableFigureMapAccessor<CssFont>
        implements NonnullMapAccessor<CssFont> {

    private final static long serialVersionUID = 1L;

    @Nonnull
    private final CssMetaData<?, CssFont> cssMetaData;
    @Nonnull
    private final MapAccessor<String> familyKey;
    @Nonnull
    private final MapAccessor<FontWeight> weightKey;
    @Nonnull
    private final MapAccessor<FontPosture> postureKey;
    @Nonnull
    private final MapAccessor<CssSize> sizeKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name       the name of the accessor
     * @param familyKey  the font family key
     * @param weightKey  the font weight key
     * @param postureKey the font posture key
     * @param sizeKey    the font size key
     */
    public FontStyleableMapAccessor(String name,
                                    MapAccessor<String> familyKey, MapAccessor<FontWeight> weightKey,
                                    MapAccessor<FontPosture> postureKey, MapAccessor<CssSize> sizeKey) {
        super(name, CssFont.class, new MapAccessor<?>[]{familyKey, sizeKey, weightKey, postureKey},
                CssFont.font(familyKey.getDefaultValue(), weightKey.getDefaultValue(), postureKey.getDefaultValue(),
                        sizeKey.getDefaultValue()));

        Function<Styleable, StyleableProperty<CssFont>> function = s -> {
            StyleablePropertyBean spb = (StyleablePropertyBean) s;
            return spb.getStyleableProperty(this);
        };
        boolean inherits = false;
        String property = Figure.JHOTDRAW_CSS_PREFIX + getCssName();
        CssMetaData<Styleable, CssFont> md
                = new SimpleCssMetaData<>(property, function,
                new StyleConverterAdapter<>(converter), getDefaultValue(), inherits);
        cssMetaData = md;

        this.familyKey = familyKey;
        this.sizeKey = sizeKey;
        this.weightKey = weightKey;
        this.postureKey = postureKey;
    }

    @Nonnull
    @Override
    public CssMetaData<?, CssFont> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<CssFont> converter = new CssFontConverter(false);
    ;

    @Override
    public Converter<CssFont> getConverter() {
        return converter;
    }

    @Override
    public CssFont get(@Nonnull Map<? super Key<?>, Object> a) {
        CssFont f = CssFont.font(familyKey.get(a), weightKey.get(a), postureKey.get(a), sizeKey.get(a));
        return f;
    }

    @Override
    public CssFont put(@Nonnull Map<? super Key<?>, Object> a, @Nullable CssFont value) {
        CssFont oldValue = get(a);
        if (value == null) {
            familyKey.put(a, null);
            weightKey.put(a, null);
            postureKey.put(a, null);
            sizeKey.put(a, null);
        } else {
            familyKey.put(a, value.getFamily());
            weightKey.put(a, value.getWeight());
            postureKey.put(a, value.getPosture());
            sizeKey.put(a, value.getSize());
        }
        return oldValue;
    }

    @Override
    public CssFont remove(@Nonnull Map<? super Key<?>, Object> a) {
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
