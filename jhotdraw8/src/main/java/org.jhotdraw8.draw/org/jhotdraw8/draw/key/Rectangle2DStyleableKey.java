/*
 * @(#)Rectangle2DStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.geometry.Rectangle2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.text.Rectangle2DConverter;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * Rectangle2DStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class Rectangle2DStyleableKey extends AbstractStyleableKey<Rectangle2D> implements WritableStyleableMapAccessor<Rectangle2D> {

    private static final long serialVersionUID = 1L;
    private Converter<Rectangle2D> converter = new Rectangle2DConverter(false);

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public Rectangle2DStyleableKey(@NonNull String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param key         The name of the key.
     * @param defaultValue The default value.
     */
    public Rectangle2DStyleableKey(@NonNull String key, Rectangle2D defaultValue) {
        super(key, Rectangle2D.class, defaultValue);

    }

    @Override
    public @NonNull Converter<Rectangle2D> getCssConverter() {
        return converter;
    }
}
