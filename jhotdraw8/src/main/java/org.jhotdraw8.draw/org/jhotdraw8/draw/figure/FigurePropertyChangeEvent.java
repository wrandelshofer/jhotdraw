/*
 * @(#)FigurePropertyChangeEvent.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.event.Event;

/**
 * FigurePropertyChangeEvent.
 *
 * @author Werner Randelshofer
 */
public class FigurePropertyChangeEvent extends Event<Figure> {

    private static final long serialVersionUID = 1L;
    private final @NonNull Key<?> key;
    private final @Nullable Object oldValue;
    private final @Nullable Object newValue;

    public <T> FigurePropertyChangeEvent(@NonNull Figure source, @NonNull Key<T> key, @Nullable T oldValue, @Nullable T newValue) {
        super(source);
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Returns the key of the property that has changed.
     *
     * @return the key or null
     */
    public @NonNull Key<?> getKey() {
        return key;
    }

    public @Nullable <T> T getOldValue() {
        @SuppressWarnings("unchecked") T oldValue = (T) this.oldValue;
        return oldValue;
    }

    public @Nullable <T> T getNewValue() {
        @SuppressWarnings("unchecked") T newValue = (T) this.newValue;
        return newValue;
    }

}
