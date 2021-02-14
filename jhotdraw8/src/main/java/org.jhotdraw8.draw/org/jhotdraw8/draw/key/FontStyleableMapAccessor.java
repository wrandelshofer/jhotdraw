/*
 * @(#)FontStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssFontConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.text.StyleConverterAdapter;

import java.util.Map;
import java.util.function.Function;

/**
 * FontStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class FontStyleableMapAccessor extends AbstractStyleableMapAccessor<@NonNull CssFont>
        implements NonNullMapAccessor<@NonNull CssFont> {

    private static final long serialVersionUID = 1L;

    private final @NonNull CssMetaData<?, @NonNull CssFont> cssMetaData;
    private final @NonNull MapAccessor<String> familyKey;
    private final @NonNull MapAccessor<FontWeight> weightKey;
    private final @NonNull MapAccessor<FontPosture> postureKey;
    private final @NonNull MapAccessor<@NonNull CssSize> sizeKey;

    /**
     * Creates a new instance with the specified name.
     *
     * @param name       the name of the accessor
     * @param familyKey  the font family key
     * @param weightKey  the font weight key
     * @param postureKey the font posture key
     * @param sizeKey    the font size key
     */
    public FontStyleableMapAccessor(@NonNull String name,
                                    @NonNull MapAccessor<String> familyKey, @NonNull MapAccessor<FontWeight> weightKey,
                                    @NonNull MapAccessor<FontPosture> postureKey, @NonNull MapAccessor<@NonNull CssSize> sizeKey) {
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

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, CssFont> getCssMetaData() {
        return cssMetaData;

    }

    private final Converter<CssFont> converter = new CssFontConverter(false);

    @Override
    public @NonNull Converter<CssFont> getCssConverter() {
        return converter;
    }

    @Override
    public CssFont get(@NonNull Map<? super Key<?>, Object> a) {
        CssFont f = CssFont.font(familyKey.get(a), weightKey.get(a), postureKey.get(a), sizeKey.get(a));
        return f;
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @Nullable CssFont value) {
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
    }

    @Override
    public CssFont remove(@NonNull Map<? super Key<?>, Object> a) {
        CssFont oldValue = get(a);
        familyKey.remove(a);
        weightKey.remove(a);
        postureKey.remove(a);
        sizeKey.remove(a);
        return oldValue;
    }
}
