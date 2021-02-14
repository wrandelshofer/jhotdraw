/*
 * @(#)BlendModeStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.effect.BlendMode;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleablePropertyBean;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * BlendModeStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class BlendModeStyleableKey extends AbstractStyleableKey<BlendMode> implements WriteableStyleableMapAccessor<BlendMode> {

    static final long serialVersionUID = 1L;

    private final CssMetaData<? extends Styleable, BlendMode> cssMetaData;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public BlendModeStyleableKey(@NonNull String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public BlendModeStyleableKey(@NonNull String name, BlendMode defaultValue) {
        super(name, BlendMode.class, defaultValue);

        StyleablePropertyFactory<? extends Styleable> factory = new StyleablePropertyFactory<>(null);
        cssMetaData = factory.createEnumCssMetaData(BlendMode.class,
                Figure.JHOTDRAW_CSS_PREFIX + getCssName(), s -> {
                    StyleablePropertyBean spb = (StyleablePropertyBean) s;
                    return spb.getStyleableProperty(this);
                });
    }

    @Override
    public @NonNull CssMetaData<? extends @NonNull Styleable, BlendMode> getCssMetaData() {
        return cssMetaData;

    }

    private Converter<BlendMode> converter;

    @Override
    public @NonNull Converter<BlendMode> getCssConverter() {
        if (converter == null) {
            converter = new CssEnumConverter<>(BlendMode.class, false);
        }
        return converter;
    }

}
