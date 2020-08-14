/*
 * @(#)Inheritable.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

/**
 * Wraps a value that supports CSS defaulting.
 *
 * @param <T>
 */
public class CssDefaultableValue<T> {
    private final @Nullable CssDefaulting defaulting;
    private final @Nullable T value;

    public CssDefaultableValue(@Nullable CssDefaulting defaulting, @Nullable T value) {
        this.defaulting = defaulting;
        this.value = value;
    }

    public CssDefaultableValue(@NonNull CssDefaulting defaulting) {
        this(defaulting, null);
    }

    public CssDefaultableValue(@Nullable T value) {
        this(null, value);
    }

    public @Nullable CssDefaulting getDefaulting() {
        return defaulting;
    }

    public @Nullable T getValue() {
        return value;
    }
}
