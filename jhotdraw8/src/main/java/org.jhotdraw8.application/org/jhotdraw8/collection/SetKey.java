/*
 * @(#)SetKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.reflect.TypeToken;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 */
public class SetKey<E> extends ObjectKey<ImmutableSet<E>> {

    private static final long serialVersionUID = 1L;

    public SetKey(String key, TypeToken<ImmutableSet<E>> type) {
        super(key, type, ImmutableSets.emptySet());
    }

    public SetKey(String key, TypeToken<ImmutableSet<E>> type, ImmutableSet<E> defaultValue) {
        super(key, type, defaultValue);
    }
}
