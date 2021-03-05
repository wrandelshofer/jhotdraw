/*
 * @(#)SvgDefaultablePaint.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.Paintable;

import java.util.Objects;

/**
 * Wraps a value that supports CSS defaulting.
 *
 * @param <T> the value type
 * @see SvgPaintDefaulting
 */
public class SvgDefaultablePaint<T extends Paintable> {
    private final @Nullable SvgPaintDefaulting defaulting;
    private final @Nullable T value;

    public SvgDefaultablePaint(@Nullable SvgPaintDefaulting defaulting, @Nullable T value) {
        this.defaulting = defaulting;
        this.value = value;
    }

    public SvgDefaultablePaint(@NonNull SvgPaintDefaulting defaulting) {
        this(defaulting, null);
    }

    public SvgDefaultablePaint(@Nullable T value) {
        this(null, value);
    }

    public @Nullable SvgPaintDefaulting getDefaulting() {
        return defaulting;
    }

    public @Nullable T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CssDefaultablePaint{" +
                /*"defaulting=" +*/ defaulting +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SvgDefaultablePaint<?> that = (SvgDefaultablePaint<?>) o;
        return defaulting == that.defaulting &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaulting, value);
    }
}
