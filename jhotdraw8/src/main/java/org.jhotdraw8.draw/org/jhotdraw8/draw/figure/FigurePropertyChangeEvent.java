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

    private final static long serialVersionUID = 1L;
    @NonNull
    private final Key<?> key;
    @Nullable
    private final Object oldValue;
    @Nullable
    private final Object newValue;

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
    @NonNull
    public Key<?> getKey() {
        return key;
    }

    @Nullable
    public <T> T getOldValue() {
        @SuppressWarnings("unchecked") T oldValue = (T) this.oldValue;
        return oldValue;
    }

    @Nullable
    public <T> T getNewValue() {
        @SuppressWarnings("unchecked") T newValue = (T) this.newValue;
        return newValue;
    }

}
