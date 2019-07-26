/*
 * @(#)ObservableWordListKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.collection.ImmutableList;

/**
 * ObservableWordListKey.
 *
 * @author Werner Randelshofer
 */
public class ObservableWordListKey extends ObjectFigureKey<ImmutableList<String>> {

    private final static long serialVersionUID = 1L;

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public ObservableWordListKey(String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name and default value.
     *
     * @param name         The name of the key.
     * @param defaultValue The default value.
     */
    public ObservableWordListKey(String name, ImmutableList<String> defaultValue) {
        this(name, DirtyMask.of(DirtyBits.NODE), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, mask and default value.
     *
     * @param name         The name of the key.
     * @param mask         The dirty mask.
     * @param defaultValue The default value.
     */
    public ObservableWordListKey(String name, DirtyMask mask, ImmutableList<String> defaultValue) {
        super(name, ImmutableList.class, new Class<?>[]{String.class}, defaultValue);
    }
}
