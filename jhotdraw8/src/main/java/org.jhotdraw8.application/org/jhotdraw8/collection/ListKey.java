/* @(#)DoubleKey.java
 * Copyright © by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * DoubleKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ListKey<E> extends ObjectKey<ImmutableList<E>> {

    private final static long serialVersionUID = 1L;

    public ListKey(String key, Class<E> elemClass) {
        super(key, ImmutableList.class, new Class<?>[]{elemClass}, ImmutableList.emptyList());
    }

    public ListKey(String key, Class<E> elemClass, ImmutableList<E> defaultValue) {
        super(key, ImmutableList.class, new Class<?>[]{elemClass}, defaultValue);
    }
}
