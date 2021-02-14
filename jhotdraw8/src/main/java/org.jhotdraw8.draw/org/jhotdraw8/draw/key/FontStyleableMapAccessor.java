/*
 * @(#)FontStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

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
import org.jhotdraw8.text.Converter;

import java.util.Map;

/**
 * FontStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class FontStyleableMapAccessor extends AbstractStyleableMapAccessor<@NonNull CssFont>
        implements NonNullMapAccessor<@NonNull CssFont> {

    private static final long serialVersionUID = 1L;

    private final @NonNull MapAccessor<String> familyKey;
    private final @NonNull MapAccessor<FontWeight> weightKey;
    private final @NonNull MapAccessor<FontPosture> postureKey;
    private final @NonNull MapAccessor<@NonNull CssSize> sizeKey;
    private final Converter<CssFont> converter = new CssFontConverter(false);

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

        this.familyKey = familyKey;
        this.sizeKey = sizeKey;
        this.weightKey = weightKey;
        this.postureKey = postureKey;
    }

    @Override
    public CssFont get(@NonNull Map<? super Key<?>, Object> a) {
        CssFont f = CssFont.font(familyKey.get(a), weightKey.get(a), postureKey.get(a), sizeKey.get(a));
        return f;
    }

    @Override
    public @NonNull Converter<CssFont> getCssConverter() {
        return converter;
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
}
