/*
 * @(#)InsetsStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.geometry.Insets;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.text.InsetsConverter;
import org.jhotdraw8.text.Converter;

import java.util.Map;

/**
 * InsetsStyleableMapAccessor.
 *
 * @author Werner Randelshofer
 */
public class InsetsStyleableMapAccessor extends AbstractStyleableMapAccessor<Insets> {

    private static final long serialVersionUID = 1L;

    private final @NonNull MapAccessor<Double> topKey;
    private final @NonNull MapAccessor<Double> rightKey;
    private final @NonNull MapAccessor<Double> bottomKey;
    private final @NonNull MapAccessor<Double> leftKey;
    private Converter<Insets> converter = new InsetsConverter(false);

    /**
     * Creates a new instance with the specified name.
     *
     * @param name      the name of the accessor
     * @param topKey    the insets top key
     * @param rightKey  the insets right key
     * @param bottomKey the insets bottom key
     * @param leftKey   the insets left key
     */
    public InsetsStyleableMapAccessor(String name, @NonNull MapAccessor<Double> topKey, @NonNull MapAccessor<Double> rightKey, @NonNull MapAccessor<Double> bottomKey, @NonNull MapAccessor<Double> leftKey) {
        super(name, Insets.class, new MapAccessor<?>[]{topKey, rightKey, bottomKey, leftKey}, new Insets(topKey.getDefaultValue(), rightKey.getDefaultValue(), bottomKey.getDefaultValue(), leftKey.getDefaultValue()));

        this.topKey = topKey;
        this.rightKey = rightKey;
        this.bottomKey = bottomKey;
        this.leftKey = leftKey;
    }

    @Override
    public @NonNull Insets get(@NonNull Map<? super Key<?>, Object> a) {
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

    @Override
    public @NonNull Converter<Insets> getCssConverter() {
        return converter;
    }

    @Override
    public @NonNull Insets remove(@NonNull Map<? super Key<?>, Object> a) {
        Insets oldValue = get(a);
        topKey.remove(a);
        rightKey.remove(a);
        bottomKey.remove(a);
        leftKey.remove(a);
        return oldValue;
    }

    @Override
    public void set(@NonNull Map<? super Key<?>, Object> a, @NonNull Insets value) {
        topKey.put(a, value.getTop());
        rightKey.put(a, value.getRight());
        bottomKey.put(a, value.getBottom());
        leftKey.put(a, value.getLeft());
    }
}
