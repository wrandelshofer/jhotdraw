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
public class SetKey<E> extends SimpleKey<ImmutableObservableSet<E>> {

    private final static long serialVersionUID = 1L;

    public SetKey(String key, Class<E> elemClass) {
        super(key, ImmutableObservableSet.class, new Class<?>[]{elemClass},ImmutableObservableSet.emptySet());
    }

    public SetKey(String key, Class<E> elemClass, ImmutableObservableSet<E> defaultValue) {
        super(key, ImmutableObservableList.class, new Class<?>[]{elemClass},defaultValue);
    }
}
