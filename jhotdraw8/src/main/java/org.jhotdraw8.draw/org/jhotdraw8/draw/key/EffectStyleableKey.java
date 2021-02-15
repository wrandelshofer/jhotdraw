/*
 * @(#)EffectStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.scene.effect.Effect;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.CssEffectConverter;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * EffectStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class EffectStyleableKey extends AbstractStyleableKey<Effect> implements WritableStyleableMapAccessor<Effect> {

    static final long serialVersionUID = 1L;
    private final CssEffectConverter converter = new CssEffectConverter();

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public EffectStyleableKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public EffectStyleableKey(String name, Effect defaultValue) {
        super(name, Effect.class, defaultValue);
    }

    @Override
    public @NonNull Converter<Effect> getCssConverter() {
        return converter;
    }
}
