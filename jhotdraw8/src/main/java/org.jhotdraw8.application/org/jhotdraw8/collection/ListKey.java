/*
 * @(#)ListKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.reflect.TypeToken;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 */
public class ListKey<E> extends ObjectKey<ImmutableList<E>> {

    private final static long serialVersionUID = 1L;

    public ListKey(String key, TypeToken<ImmutableList<E>> type) {
        super(key, type, ImmutableLists.emptyList());
    }

    public ListKey(String key, TypeToken<ImmutableList<E>> type, ImmutableList<E> defaultValue) {
        super(key, type, defaultValue);
    }
}
