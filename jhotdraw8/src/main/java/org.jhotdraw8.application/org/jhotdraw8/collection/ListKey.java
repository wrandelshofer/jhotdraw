/*
 * @(#)ListKey.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 */
public class ListKey<E> extends ObjectKey<ImmutableList<E>> {

    private final static long serialVersionUID = 1L;

    public ListKey(String key, Class<E> elemClass) {
        super(key, ImmutableList.class, new Class<?>[]{elemClass}, ImmutableLists.emptyList());
    }

    public ListKey(String key, Class<E> elemClass, ImmutableList<E> defaultValue) {
        super(key, ImmutableList.class, new Class<?>[]{elemClass}, defaultValue);
    }
}
