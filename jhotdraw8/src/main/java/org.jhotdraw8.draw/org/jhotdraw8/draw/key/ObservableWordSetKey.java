/*
 * @(#)ObservableWordListKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableSet;
import org.jhotdraw8.reflect.TypeToken;

/**
 * ObservableWordSetKey.
 *
 * @author Werner Randelshofer
 */
public class ObservableWordSetKey extends ObjectFigureKey<@NonNull ImmutableSet<String>> {

    private final static long serialVersionUID = 1L;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public ObservableWordSetKey(@NonNull String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *  @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public ObservableWordSetKey(@NonNull String name, @NonNull ImmutableSet<String> defaultValue) {
        super(name, new TypeToken<ImmutableSet<String>>() {
        }, defaultValue);
    }
}
