/* @(#)DoubleKey.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ListKey<E> extends SimpleKey<ImmutableObservableList<E>> {

    private final static long serialVersionUID = 1L;

    public ListKey(String key, Class<E> elemClass) {
        super(key, ImmutableObservableList.class, new Class<?>[]{elemClass},ImmutableObservableList.emptyList());
    }

    public ListKey(String key, Class<E> elemClass, ImmutableObservableList<E> defaultValue) {
        super(key, ImmutableObservableList.class, new Class<?>[]{elemClass},defaultValue);
    }
}
