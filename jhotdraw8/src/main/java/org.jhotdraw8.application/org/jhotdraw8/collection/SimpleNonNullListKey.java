/*
 * @(#)ListKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.reflect.TypeToken;

/**
 * An abstract {@link Key} that stores a list of values.
 *
 * @author Werner Randelshofer
 */
public class SimpleNonNullListKey<E> extends SimpleNonNullKey<ImmutableList<E>> {

    private static final long serialVersionUID = 1L;

    public SimpleNonNullListKey(String key, TypeToken<ImmutableList<E>> type) {
        super(key, type, ImmutableLists.emptyList());
    }

    public SimpleNonNullListKey(String key, TypeToken<ImmutableList<E>> type, @NonNull ImmutableList<E> defaultValue) {
        super(key, type, defaultValue);
    }
}
