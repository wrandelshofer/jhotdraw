/* @(#)DoubleKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SetKey<E> extends ObjectKey<ImmutableSet<E>> {

    private final static long serialVersionUID = 1L;

    public SetKey(String key, Class<E> elemClass) {
        super(key, ImmutableSet.class, new Class<?>[]{elemClass}, ImmutableSet.emptySet());
    }

    public SetKey(String key, Class<E> elemClass, ImmutableSet<E> defaultValue) {
        super(key, ImmutableArrayList.class, new Class<?>[]{elemClass},defaultValue);
    }
}
